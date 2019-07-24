grammar Larl;

modules: module ('\n'+ module)* EOF;

module: MODULE ' ' PackageIdentifier emptyLine moduleBody;

moduleBody: emptyLine* model (emptyLine | models)*;

models: model (emptyLine | model)*;

model: ObjectIdentifier emptyLine* modelBody;

modelBody: (emptyLine | fieldLine)*;

emptyLine: '\n' ' '*;

fieldLine: '  ' field;

field: fieldWithoutDefault | fieldWithDefault;

fieldWithDefault: fieldWithoutDefault ' ' defaultArgument;

defaultArgument: DefaultArgument;

fieldWithoutDefault: typeDefinition ' ' FieldIdentifier;

typeDefinition: type;

type: (builtinType | nestedType | customType);

customType: ObjectIdentifier;

nestedType: (arrayType | vectorType | tupleType);

arrayType: ARRAY_T '<' type '>' '[' Number ']';

vectorType: VECTOR_T '<' type '>';

tupleType: TUPLE_T '<' type (',' ' ' type)+ '>';

builtinType:
    ( BOOL_T
    | CHAR_T
    | I8_T
    | I16_T
    | I32_T
    | I64_T
    | ISIZE_T
    | U8_T
    | U16_T
    | U32_T
    | U64_T
    | USIZE_T
    | F32_T
    | F64_T
    | STR_T
    );

/* Keywords */

MODULE: 'module';
TRUE: 'true';
FALSE: 'false';

/* Builtin Types */

BOOL_T: 'bool';
CHAR_T: 'char';
I8_T: 'i8';
I16_T: 'i16';
I32_T: 'i32';
I64_T: 'i64';
ISIZE_T: 'isize';
U8_T: 'u8';
U16_T: 'u16';
U32_T: 'u32';
U64_T: 'u64';
USIZE_T: 'usize';
F32_T: 'f32';
F64_T: 'f64';
ARRAY_T: 'array';
VECTOR_T: 'vec';
STR_T: 'str';
TUPLE_T: 'tuple';

/* Identifiers */

ObjectIdentifier: UpperAlpha AlphaNum*;
FieldIdentifier: LowerAlpha AlphaNum*;
PackageIdentifier: Separator (Chars | Separator)* Chars;
DefaultArgument: '{' ' '* ~('{' | '}')+ ' '* '}';
Number: Digit+;
fragment Separator: '/';
fragment Chars: AlphaNum | Symbols;
fragment AlphaNum: LowerAlpha | UpperAlpha | Digit;
fragment Symbols: '-' | '_' | '.' | ',' | ';' | ':' | '$' | '!' | '+' | '=' | '*';
fragment LowerAlpha: 'a'..'z';
fragment UpperAlpha: 'A' .. 'Z';
fragment Digit: '0'..'9';

