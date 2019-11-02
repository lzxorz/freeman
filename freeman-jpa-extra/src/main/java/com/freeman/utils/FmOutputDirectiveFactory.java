package com.freeman.utils;

import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.stat.Location;
import com.jfinal.template.stat.OutputDirectiveFactory;
import com.jfinal.template.stat.ast.Output;

public class FmOutputDirectiveFactory extends OutputDirectiveFactory {
    public static final OutputDirectiveFactory me = new FmOutputDirectiveFactory();

    public Output getOutputDirective(ExprList exprList, Location location) {
        return new FmOutput(exprList, location);
    }

}
