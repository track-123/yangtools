/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.model.util;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opendaylight.yangtools.yang.common.QName;
import org.opendaylight.yangtools.yang.model.api.Module;
import org.opendaylight.yangtools.yang.model.api.RevisionAwareXPath;
import org.opendaylight.yangtools.yang.model.api.SchemaContext;
import org.opendaylight.yangtools.yang.model.api.SchemaNode;
import org.opendaylight.yangtools.yang.model.api.SchemaPath;
import org.opendaylight.yangtools.yang.model.util.type.BaseTypes;

public class SchemaContextUtilTest {
    @Mock
    private SchemaContext mockSchemaContext;
    @Mock
    private Module mockModule;

    @Test
    public void testFindDummyData() {
        MockitoAnnotations.initMocks(this);

        QName qname = QName.create("TestQName");
        SchemaPath schemaPath = SchemaPath.create(Collections.singletonList(qname), true);
        assertEquals("Should be null. Module TestQName not found", null,
                SchemaContextUtil.findDataSchemaNode(mockSchemaContext, schemaPath));

        RevisionAwareXPath xpath = new RevisionAwareXPathImpl("/bookstore/book/title", true);
        assertEquals("Should be null. Module bookstore not found", null,
                SchemaContextUtil.findDataSchemaNode(mockSchemaContext, mockModule, xpath));

        SchemaNode schemaNode = BaseTypes.int32Type();
        RevisionAwareXPath xpathRelative = new RevisionAwareXPathImpl("../prefix", false);
        assertEquals("Should be null, Module prefix not found", null,
                SchemaContextUtil.findDataSchemaNodeForRelativeXPath(
                        mockSchemaContext, mockModule, schemaNode, xpathRelative));

        assertEquals("Should be null. Module TestQName not found", null,
                SchemaContextUtil.findNodeInSchemaContext(mockSchemaContext, Collections.singleton(qname)));

        assertEquals("Should be null.", null, SchemaContextUtil.findParentModule(mockSchemaContext, schemaNode));
    }
}
