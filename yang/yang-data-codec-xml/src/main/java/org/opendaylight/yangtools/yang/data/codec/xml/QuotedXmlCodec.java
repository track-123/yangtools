/*
 * Copyright (c) 2016 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.yangtools.yang.data.codec.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.opendaylight.yangtools.yang.data.impl.codec.DataStringCodec;

final class QuotedXmlCodec<T> extends AbstractXmlCodec<T> {
    QuotedXmlCodec(final DataStringCodec<T> codec) {
        super(codec);
    }

    /**
     * Serialize specified value with specified XMLStreamWriter.
     *
     * @param writer XMLStreamWriter
     * @param value value which will be serialized to the writer
     */
    @Override
    public void serializeToWriter(final XMLStreamWriter writer, final T value) throws XMLStreamException {
        writer.writeCharacters(serialize(value));
    }
}
