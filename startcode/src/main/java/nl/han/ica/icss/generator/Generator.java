package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.AST;

public class Generator {

    public String generate(final AST ast) {
        return ast.root.toString();
    }
}
