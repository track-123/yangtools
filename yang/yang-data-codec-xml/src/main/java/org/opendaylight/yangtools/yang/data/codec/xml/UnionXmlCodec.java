/*
 * Copyright (c) 2016 Intel Corporation and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.yangtools.yang.data.codec.xml;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.opendaylight.yangtools.yang.model.api.type.UnionTypeDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class UnionXmlCodec<T> implements XmlCodec<T> {
    private static final class Diverse extends UnionXmlCodec<Object> {
        Diverse(final List<XmlCodec<?>> codecs) {
            super(codecs);
        }

        @Override
        public Class<Object> getDataClass() {
            return Object.class;
        }
    }

    private static final class SingleType<T> extends UnionXmlCodec<T> {
        private final Class<T> dataClass;

        SingleType(final Class<T> dataClass, final List<XmlCodec<?>> codecs) {
            super(codecs);
            this.dataClass = Preconditions.checkNotNull(dataClass);
        }

        @Override
        public Class<T> getDataClass() {
            return dataClass;
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(UnionXmlCodec.class);

    private final List<XmlCodec<?>> codecs;

    UnionXmlCodec(final List<XmlCodec<?>> codecs) {
        this.codecs = ImmutableList.copyOf(codecs);
    }

    static UnionXmlCodec<?> create(final UnionTypeDefinition type, final List<XmlCodec<?>> codecs) {
        final Iterator<XmlCodec<?>> it = codecs.iterator();
        Verify.verify(it.hasNext(), "Union %s has no subtypes", type);

        Class<?> dataClass = it.next().getDataClass();
        while (it.hasNext()) {
            final Class<?> next = it.next().getDataClass();
            if (!dataClass.equals(next)) {
                LOG.debug("Type {} has diverse data classes: {} and {}", type, dataClass, next);
                return new Diverse(codecs);
            }
        }

        LOG.debug("Type {} has single data class {}", type, dataClass);
        return new SingleType<>(dataClass, codecs);
    }

    @Override
    public final T deserializeFromString(final NamespaceContext namespaceContext, final String input) {
        for (XmlCodec<?> codec : codecs) {
            final Object ret;
            try {
                ret = codec.deserializeFromString(namespaceContext, input);
            } catch (RuntimeException e) {
                LOG.debug("Codec {} did not accept input '{}'", codec, input, e);
                continue;
            }

            return getDataClass().cast(ret);
        }

        throw new IllegalArgumentException("Invalid value \"" + input + "\" for union type.");
    }

    @Override
    public void serializeToWriter(final XMLStreamWriter writer, final Object value) throws XMLStreamException {
        for (XmlCodec<?> codec : codecs) {
            if (!codec.getDataClass().isInstance(value)) {
                LOG.debug("Codec {} cannot accept input {}, skipping it", codec, value);
                continue;
            }

            @SuppressWarnings("unchecked")
            final XmlCodec<Object> objCodec = (XmlCodec<Object>) codec;
            try {
                objCodec.serializeToWriter(writer, value);
                return;
            } catch (RuntimeException e) {
                LOG.debug("Codec {} failed to serialize {}", codec, value, e);
            }
        }

        throw new IllegalArgumentException("No codecs could serialize" + value);
    }
}
