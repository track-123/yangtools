/**
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.parser.stmt.rfc6020.effective.type;

import java.util.List;
import org.opendaylight.yangtools.yang.model.api.stmt.LengthStatement;
import org.opendaylight.yangtools.yang.model.api.type.LengthConstraint;
import org.opendaylight.yangtools.yang.parser.spi.meta.StmtContext;

public class LengthEffectiveStatementImpl extends
        AbstractListConstraintEffectiveStatement<LengthConstraint, LengthStatement> {

    public LengthEffectiveStatementImpl(final StmtContext<List<LengthConstraint>, LengthStatement, ?> ctx) {
        super(ctx);
    }

    @Override
    final LengthConstraint createCustomizedConstraint(final LengthConstraint lengthConstraint) {
        return new LengthConstraintEffectiveImpl(lengthConstraint.getMin(), lengthConstraint.getMax(),
                getDescription(), getReference(), getErrorAppTag(), getErrorMessage());
    }
}