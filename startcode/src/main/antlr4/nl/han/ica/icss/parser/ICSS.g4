grammar ICSS;

//--- LEXER: ---
// IF support:
IF: 'if';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';

//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;

//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';

//--- PARSER: ---

stylesheet : var* styleRule+  EOF;

// VARIABLES
var : varRef ASSIGNMENT_OPERATOR literal SEMICOLON;
varRef : CAPITAL_IDENT;

// STYLE DEFINITIONS
styleRule : selector body;

selector : tagSelector | idSelector | classSelector;
tagSelector : LOWER_IDENT;
idSelector : ID_IDENT;
classSelector : CLASS_IDENT;

body : OPEN_BRACE declaration+ CLOSE_BRACE;

declaration : nameProperty COLON expression SEMICOLON;

nameProperty : LOWER_IDENT;

expression : value | operation;

value : literal | varRef;

operation : value #valueExpression | operation MUL operation #multiplyOperation | operation PLUS operation #addOperation | operation MIN operation #subtractOperation;

literal : PIXELSIZE #PixelLiteral | COLOR #ColorLiteral | (TRUE | FALSE) #BoolLiteral | PERCENTAGE #PercentageLiteral | SCALAR #ScalarLiteral;