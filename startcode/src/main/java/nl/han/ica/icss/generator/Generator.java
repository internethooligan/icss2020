package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.AST;

public class Generator {

    public String generate(final AST ast) {
        // Heb de classes uit de startcode aangepast hiervoor
        return ast.root.toString();
    }
}
