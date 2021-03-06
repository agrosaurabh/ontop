/*
 * Turtle.g
 * Copyright (C) 2010 Obdalib Team
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * For information on how to redistribute this software under
 * the terms of a license other than GNU General Public License
 * contact TMate Software at support@sqljet.com
 *
 * @author Josef Hardi (josef.hardi@unibz.it)
 */
grammar TurtleOBDA;

@header {
package it.unibz.krdb.obda.parser;

import it.unibz.krdb.obda.model.CQIE;
import it.unibz.krdb.obda.model.Function;
import it.unibz.krdb.obda.model.NewLiteral;
import it.unibz.krdb.obda.model.OBDADataFactory;
import it.unibz.krdb.obda.model.OBDALibConstants;
import it.unibz.krdb.obda.model.Predicate;
import it.unibz.krdb.obda.model.URIConstant;
import it.unibz.krdb.obda.model.ValueConstant;
import it.unibz.krdb.obda.model.Variable;
import it.unibz.krdb.obda.model.Predicate.COL_TYPE;
import it.unibz.krdb.obda.model.impl.OBDADataFactoryImpl;
import it.unibz.krdb.obda.model.impl.OBDAVocabulary;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.BitSet;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;

import com.hp.hpl.jena.iri.IRI;
import com.hp.hpl.jena.iri.IRIFactory;
}

@lexer::header {
package it.unibz.krdb.obda.parser;

import java.util.List;
import java.util.Vector;
}

@lexer::members {
private String error = "";
    
public String getError() {
   return error;
}

@Override
public Object recoverFromMismatchedSet(IntStream input, RecognitionException e, BitSet follow) throws RecognitionException {
   throw e;
}

@Override
public void recover(IntStream input, RecognitionException re) {
   throw new RuntimeException(error);
}
    
@Override
public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
   String hdr = getErrorHeader(e);
   String msg = getErrorMessage(e, tokenNames);
   emitErrorMessage("Syntax error: " + msg + " Location: " + hdr);
}

@Override
public void emitErrorMessage(String msg) {
   error = msg;
   throw new RuntimeException(error);
}
    
@Override
public Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow) throws RecognitionException {
   throw new RecognitionException(input);
}
}

@members {
/** Map of directives */
private HashMap<String, String> directives = new HashMap<String, String>();

/** The current subject term */
private NewLiteral subject;

/** All variables */
private Set<NewLiteral> variableSet = new HashSet<NewLiteral>();

/** A factory to construct the predicates and terms */
private static final OBDADataFactory dfac = OBDADataFactoryImpl.getInstance();

private static IRIFactory iriFactory = IRIFactory.iriImplementation();

private String error = "";

public String getError() {
   return error;
}

protected void mismatch(IntStream input, int ttype, BitSet follow) throws RecognitionException {
   throw new MismatchedTokenException(ttype, input);
}

public Object recoverFromMismatchedSet(IntStream input, RecognitionException e, BitSet follow) throws RecognitionException {
   throw e;
}

@Override
public void recover(IntStream input, RecognitionException re) {
   throw new RuntimeException(error);
}

@Override
public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
   String hdr = getErrorHeader(e);
   String msg = getErrorMessage(e, tokenNames);
   emitErrorMessage("Syntax error: " + msg + " Location: " + hdr);
}

@Override
public void emitErrorMessage(String msg) {
   error = msg;
}
    
public Object recoverFromMismatchedTokenrecoverFromMismatchedToken(IntStream input, int ttype, BitSet follow) throws RecognitionException {
   throw new RecognitionException(input);
}

private String removeBrackets(String text) {
   return text.substring(1, text.length()-1);
}

private NewLiteral construct(String text) {
   NewLiteral toReturn = null;
   final String PLACEHOLDER = "{}"; 
   List<NewLiteral> terms = new LinkedList<NewLiteral>();
   List<FormatString> tokens = parse(text);
   int size = tokens.size();
   if (size == 1) {
      FormatString token = tokens.get(0);
      if (token instanceof FixedString) {
         toReturn = dfac.getURIConstant(token.toString());
      } else if (token instanceof ColumnString) {
         ValueConstant uriTemplate = dfac.getValueConstant(PLACEHOLDER); // a single URI template
         Variable column = dfac.getVariable(token.toString());
         terms.add(0, uriTemplate);
         terms.add(column);
         toReturn = dfac.getFunctionalTerm(dfac.getUriTemplatePredicate(terms.size()), terms);
      }
   } else {
      StringBuilder sb = new StringBuilder();
      for(FormatString token : tokens) {
         if (token instanceof FixedString) { // if part of URI template
            sb.append(token.toString());
         } else if (token instanceof ColumnString) {
            sb.append(PLACEHOLDER);
            Variable column = dfac.getVariable(token.toString());
            terms.add(column);
         }
      }
      ValueConstant uriTemplate = dfac.getValueConstant(sb.toString()); // complete URI template
      terms.add(0, uriTemplate);
      toReturn = dfac.getFunctionalTerm(dfac.getUriTemplatePredicate(terms.size()), terms);
   }
   return toReturn;
}

// Column placeholder pattern
private static final String formatSpecifier = "\\{([\\w.]+)?\\}";
private static Pattern chPattern = Pattern.compile(formatSpecifier);

private List<FormatString> parse(String text) {
   List<FormatString> toReturn = new ArrayList<FormatString>();
   Matcher m = chPattern.matcher(text);
   int i = 0;
   while (i < text.length()) {
      if (m.find(i)) {
         if (m.start() != i) {
            toReturn.add(new FixedString(text.substring(i, m.start())));
         }
         String value = m.group(1);
         toReturn.add(new ColumnString(value));
         i = m.end();
      }
      else {
         toReturn.add(new FixedString(text.substring(i)));
         break;
      }
   }
   return toReturn;
}

private interface FormatString {
   int index();
   String toString();
}

private class FixedString implements FormatString {
   private String s;
   FixedString(String s) { this.s = s; }
   @Override public int index() { return -1; }  // flag code for fixed string
   @Override public String toString() { return s; }
}

private class ColumnString implements FormatString {
   private String s;
   ColumnString(String s) { this.s = s; }
   @Override public int index() { return 0; }  // flag code for column string
   @Override public String toString() { return s; }
}
}

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/

parse returns [CQIE value]
  : directiveStatement*
    t1=triplesStatement {
      int arity = variableSet.size();
      List<NewLiteral> distinguishVariables = new ArrayList<NewLiteral>(variableSet);
      Function head = dfac.getAtom(dfac.getPredicate(OBDALibConstants.QUERY_HEAD_URI, arity, null), distinguishVariables);
      
      // Create a new rule
      List<Function> triples = $t1.value;
      $value = dfac.getCQIE(head, triples);
    }
    (t2=triplesStatement)* EOF {
      List<Function> additionalTriples = $t2.value;
      if (additionalTriples != null) {
        // If there are additional triple statements then just add to the existing body
        List<Function> existingBody = $value.getBody();
        existingBody.addAll(additionalTriples);
      }
    }
  ;

directiveStatement
  : directive PERIOD
  ;

triplesStatement returns [List<Function> value]
  : triples WS* PERIOD { $value = $triples.value; }
  ;

directive
  : base
  | prefixID
  ;

base
  : AT BASE uriref
  ;

prefixID
@init {
  String prefix = "";
}
  : AT PREFIX (namespace { prefix = $namespace.text; } | defaultNamespace { prefix = $defaultNamespace.text; }) uriref {
      String uriref = $uriref.value;
      directives.put(prefix.substring(0, prefix.length()-1), uriref); // remove the end colon
    }
  ;

triples returns [List<Function> value]
  : subject { subject = $subject.value; } predicateObjectList {
      $value = $predicateObjectList.value;
    }
  ;

predicateObjectList returns [List<Function> value]
@init {
   $value = new LinkedList<Function>();
}
  : v1=verb l1=objectList {
      for (NewLiteral object : $l1.value) {
        Function atom = null;
        String p = $v1.value.toString();
        if (p.equals(OBDAVocabulary.RDF_TYPE)) {
          URIConstant c = (URIConstant) object;  // it has to be a URI constant
          Predicate predicate = dfac.getClassPredicate(c.getURI());
          atom = dfac.getAtom(predicate, subject);
        } else {
          Predicate predicate = dfac.getPredicate($v1.value, 2, null); // the data type cannot be determined here!
          atom = dfac.getAtom(predicate, subject, object);
        }
        $value.add(atom);
      }
    } 
    (SEMI v2=verb l2=objectList {
      for (NewLiteral object : $l2.value) {
        Function atom = null;
        String p = $v2.value.toString();
        if (p.equals(OBDAVocabulary.RDF_TYPE)) {
          URIConstant c = (URIConstant) object;  // it has to be a URI constant
          Predicate predicate = dfac.getClassPredicate(c.getURI());
          atom = dfac.getAtom(predicate, subject);
        } else {
          Predicate predicate = dfac.getPredicate($v2.value, 2, null); // the data type cannot be determined here!
          atom = dfac.getAtom(predicate, subject, object);
        }
        $value.add(atom);
      }
    })*
  ;
  
verb returns [IRI value]
  : predicate { $value = $predicate.value; }
  | 'a' { $value = iriFactory.construct(OBDAVocabulary.RDF_TYPE); }
  ;

objectList returns [List<NewLiteral> value]
@init {
  $value = new ArrayList<NewLiteral>();
}
  : o1=object { $value.add($o1.value); } (COMMA o2=object { $value.add($o2.value); })* 
  ;

subject returns [NewLiteral value]
  : resource { $value = $resource.value; }
  | variable { $value = $variable.value; }
//  | blank
  ;

predicate returns [IRI value]
  : resource { 
      NewLiteral nl = $resource.value;
      if (nl instanceof URIConstant) {
        URIConstant c = (URIConstant) nl;
        $value = c.getURI();
      } else {
        throw new RuntimeException("Unsupported predicate syntax: " + nl.toString());
      }
    }
  ;

object returns [NewLiteral value]
  : resource { $value = $resource.value; }
  | literal  { $value = $literal.value; }
  | typedLiteral { $value = $typedLiteral.value; }
  | variable { $value = $variable.value; }
//  | blank
  ;

resource returns [NewLiteral value]
  : uriref { $value = construct($uriref.value); }
  | qname { $value = construct($qname.value); }
  ;

uriref returns [String value]
  : STRING_WITH_BRACKET { $value = removeBrackets($STRING_WITH_BRACKET.text); }
  ;

qname returns [String value]
  : PREFIXED_NAME {
      String[] tokens = $PREFIXED_NAME.text.split(":", 2);
      String uri = directives.get(tokens[0]);  // the first token is the prefix
      $value = uri + tokens[1];  // the second token is the local name
    }
  ;

blank
  : nodeID
  | BLANK
  ;

variable returns [Variable value]
  : STRING_WITH_CURLY_BRACKET {
      $value = dfac.getVariable(removeBrackets($STRING_WITH_CURLY_BRACKET.text));
      variableSet.add($value);
    }
  ;
  
function returns [Function value]
  : resource LPAREN terms RPAREN {
      String functionName = $resource.value.toString();
      int arity = $terms.value.size();
      Predicate functionSymbol = dfac.getPredicate(iriFactory.construct(functionName), arity);
      $value = dfac.getFunctionalTerm(functionSymbol, $terms.value);
    }
  ;

typedLiteral returns [Function value]
  : variable AT language {
      Predicate functionSymbol = dfac.getDataTypePredicateLiteralLang();
      Variable var = $variable.value;
      NewLiteral lang = $language.value;   
      $value = dfac.getFunctionalTerm(functionSymbol, var, lang);
    }
  | variable REFERENCE resource {
      Variable var = $variable.value;
      String functionName = $resource.value.toString();
      Predicate functionSymbol = null;
      if (functionName.equals(OBDAVocabulary.RDFS_LITERAL_URI)) {
          functionSymbol = dfac.getDataTypePredicateLiteral();
      } else if (functionName.equals(OBDAVocabulary.XSD_STRING_URI)) {
          functionSymbol = dfac.getDataTypePredicateString();
      } else if (functionName.equals(OBDAVocabulary.XSD_INTEGER_URI) || functionName.equals(OBDAVocabulary.XSD_INT_URI)) {
          functionSymbol = dfac.getDataTypePredicateInteger();
      } else if (functionName.equals(OBDAVocabulary.XSD_DECIMAL_URI)) {
          functionSymbol = dfac.getDataTypePredicateDecimal();
      } else if (functionName.equals(OBDAVocabulary.XSD_DOUBLE_URI)) {
          functionSymbol = dfac.getDataTypePredicateDouble();
      } else if (functionName.equals(OBDAVocabulary.XSD_DATETIME_URI)) {
          functionSymbol = dfac.getDataTypePredicateDateTime();
      } else if (functionName.equals(OBDAVocabulary.XSD_BOOLEAN_URI)) {
          functionSymbol = dfac.getDataTypePredicateBoolean();
      } else {
          throw new RecognitionException();
      }
      $value = dfac.getFunctionalTerm(functionSymbol, var);
     }
  ;

language returns [NewLiteral value]
  : languageTag {
    	$value = dfac.getValueConstant($languageTag.text.toLowerCase(), COL_TYPE.STRING);
    }
  | variable {
    	$value = $variable.value;
    }
  ;

terms returns [Vector<NewLiteral> value]
@init {
  $value = new Vector<NewLiteral>();
}
  : t1=term { $value.add($t1.value); } (COMMA t2=term { $value.add($t2.value); })*
  ;

term returns [NewLiteral value]
  : function { $value = $function.value; }
  | variable { $value = $variable.value; }
  | literal { $value = $literal.value; }
  ;

literal returns [NewLiteral value]
  : stringLiteral (AT language)? {
       ValueConstant constant = $stringLiteral.value;
       NewLiteral lang = $language.value;
       if (lang != null) {
         $value = dfac.getFunctionalTerm(dfac.getDataTypePredicateLiteralLang(), constant, lang);
       } else {
       	 $value = dfac.getFunctionalTerm(dfac.getDataTypePredicateLiteral(), constant);
       }
    }
  | dataTypeString { $value = $dataTypeString.value; }
  | numericLiteral { $value = $numericLiteral.value; }
  | booleanLiteral { $value = $booleanLiteral.value; }
  ;

stringLiteral returns [ValueConstant value]
  : STRING_WITH_QUOTE_DOUBLE {
      String str = $STRING_WITH_QUOTE_DOUBLE.text;
      $value = dfac.getValueConstant(str.substring(1, str.length()-1), COL_TYPE.LITERAL); // without the double quotes
    }
  ;

dataTypeString returns [NewLiteral value]
  :  stringLiteral REFERENCE resource {
      ValueConstant constant = $stringLiteral.value;
      String functionName = $resource.value.toString();
      Predicate functionSymbol = null;
      if (functionName.equals(OBDAVocabulary.RDFS_LITERAL_URI)) {
    	functionSymbol = dfac.getDataTypePredicateLiteral();
      } else if (functionName.equals(OBDAVocabulary.XSD_STRING_URI)) {
    	functionSymbol = dfac.getDataTypePredicateString();
      } else if (functionName.equals(OBDAVocabulary.XSD_INTEGER_URI)) {
     	functionSymbol = dfac.getDataTypePredicateInteger();
      } else if (functionName.equals(OBDAVocabulary.XSD_DECIMAL_URI)) {
    	functionSymbol = dfac.getDataTypePredicateDecimal();
      } else if (functionName.equals(OBDAVocabulary.XSD_DOUBLE_URI)) {
    	functionSymbol = dfac.getDataTypePredicateDouble();
      } else if (functionName.equals(OBDAVocabulary.XSD_DATETIME_URI)) {
    	functionSymbol = dfac.getDataTypePredicateDateTime();
      } else if (functionName.equals(OBDAVocabulary.XSD_BOOLEAN_URI)) {
    	functionSymbol = dfac.getDataTypePredicateBoolean();
      } else {
        throw new RuntimeException("Unknown datatype: " + functionName);
      }
      $value = dfac.getFunctionalTerm(functionSymbol, constant);
    }
  ;

numericLiteral returns [ValueConstant value]
  : numericUnsigned { $value = $numericUnsigned.value; }
  | numericPositive { $value = $numericPositive.value; }
  | numericNegative { $value = $numericNegative.value; }
  ;

nodeID
  : BLANK_PREFIX name
  ;

relativeURI // Not used
  : STRING_URI
  ;

namespace
  : NAMESPACE
  ;
  
defaultNamespace
  : COLON
  ;

name
  : VARNAME
  ;

languageTag
  : VARNAME
  ;

booleanLiteral returns [ValueConstant value]
  : TRUE  { $value = dfac.getValueConstant($TRUE.text, COL_TYPE.BOOLEAN); }
  | FALSE { $value = dfac.getValueConstant($FALSE.text, COL_TYPE.BOOLEAN); }
  ;

numericUnsigned returns [ValueConstant value]
  : INTEGER { $value = dfac.getValueConstant($INTEGER.text, COL_TYPE.INTEGER); }
  | DOUBLE  { $value = dfac.getValueConstant($DOUBLE.text, COL_TYPE.DOUBLE); }
  | DECIMAL { $value = dfac.getValueConstant($DECIMAL.text, COL_TYPE.DECIMAL); }
  ;

numericPositive returns [ValueConstant value]
  : INTEGER_POSITIVE { $value = dfac.getValueConstant($INTEGER_POSITIVE.text, COL_TYPE.INTEGER); }
  | DOUBLE_POSITIVE  { $value = dfac.getValueConstant($DOUBLE_POSITIVE.text, COL_TYPE.DOUBLE); }
  | DECIMAL_POSITIVE { $value = dfac.getValueConstant($DECIMAL_POSITIVE.text, COL_TYPE.DECIMAL); }
  ;

numericNegative returns [ValueConstant value]
  : INTEGER_NEGATIVE { $value = dfac.getValueConstant($INTEGER_NEGATIVE.text, COL_TYPE.INTEGER); }
  | DOUBLE_NEGATIVE  { $value = dfac.getValueConstant($DOUBLE_NEGATIVE.text, COL_TYPE.DOUBLE); }
  | DECIMAL_NEGATIVE { $value = dfac.getValueConstant($DECIMAL_NEGATIVE.text, COL_TYPE.DECIMAL); }
  ;

/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/

BASE: ('B'|'b')('A'|'a')('S'|'s')('E'|'e');

PREFIX: ('P'|'p')('R'|'r')('E'|'e')('F'|'f')('I'|'i')('X'|'x');

FALSE: ('F'|'f')('A'|'a')('L'|'l')('S'|'s')('E'|'e');

TRUE: ('T'|'t')('R'|'r')('U'|'u')('E'|'e');

REFERENCE:     '^^';
LTSIGN:        '<"';
RTSIGN:        '">';
SEMI:          ';';
PERIOD:        '.';
COMMA:         ',';
LSQ_BRACKET:   '[';
RSQ_BRACKET:   ']';
LCR_BRACKET:   '{';
RCR_BRACKET:   '}';
LPAREN:        '(';
RPAREN:        ')';
QUESTION:      '?';
DOLLAR:        '$';
QUOTE_DOUBLE:  '"';
QUOTE_SINGLE:  '\'';
APOSTROPHE:    '`';
UNDERSCORE:    '_';
MINUS:         '-';
ASTERISK:      '*';
AMPERSAND:     '&';
AT:            '@';
EXCLAMATION:   '!';
HASH:          '#';
PERCENT:       '%';
PLUS:          '+';
EQUALS:        '=';
COLON:         ':';
LESS:          '<';
GREATER:       '>';
SLASH:         '/';
DOUBLE_SLASH:  '//';
BACKSLASH:     '\\';
BLANK:	       '[]';
BLANK_PREFIX:  '_:';
TILDE:         '~';
CARET:         '^';

fragment ALPHA
  : 'a'..'z'
  | 'A'..'Z'
  ;

fragment DIGIT
  : '0'..'9'
  ;

fragment ALPHANUM
  : ALPHA
  | DIGIT
  ;

fragment CHAR
  : ALPHANUM
  | UNDERSCORE
  | MINUS
  | PERIOD
  ;

INTEGER
  : DIGIT+
  ;

DOUBLE
  : DIGIT+ PERIOD DIGIT* ('e'|'E') ('-'|'+')?
  | PERIOD DIGIT+ ('e'|'E') ('-'|'+')?
  | DIGIT+ ('e'|'E') ('-'|'+')?
  ;

DECIMAL
  : DIGIT+ PERIOD DIGIT+
  | PERIOD DIGIT+
  ;

INTEGER_POSITIVE
  : PLUS INTEGER
  ;

INTEGER_NEGATIVE
  : MINUS INTEGER
  ;

DOUBLE_POSITIVE
  : PLUS DOUBLE
  ;
  
DOUBLE_NEGATIVE
  : MINUS DOUBLE
  ;

DECIMAL_POSITIVE
  : PLUS DECIMAL
  ;
  
DECIMAL_NEGATIVE
  : MINUS DECIMAL
  ;
  
VARNAME
  : ALPHA CHAR*
  ;

fragment ECHAR
  : '\\' ('t' | 'b' | 'n' | 'r' | 'f' | '\\' | '"' | '\'')
  ;

fragment SCHEMA: ALPHA (ALPHANUM|PLUS|MINUS|PERIOD)*;

fragment URI_PATH: (ALPHANUM|UNDERSCORE|MINUS|COLON|PERIOD|HASH|QUESTION|SLASH);

fragment ID_START: (ALPHA|UNDERSCORE);

fragment ID_CORE: (ID_START|DIGIT);

fragment ID: ID_START (ID_CORE)*;

fragment NAME_START_CHAR: (ALPHA|UNDERSCORE);

fragment NAME_CHAR: (NAME_START_CHAR|DIGIT|UNDERSCORE|MINUS|PERIOD|HASH|QUESTION|SLASH|PERCENT|EQUALS|SEMI);	 

NCNAME
  : NAME_START_CHAR (NAME_CHAR)*
  ;

NCNAME_EXT
  : (NAME_CHAR|LCR_BRACKET|RCR_BRACKET|HASH|SLASH)* 	
  ;

NAMESPACE
  : NAME_START_CHAR (NAME_CHAR)* COLON
  ;

PREFIXED_NAME
  : NCNAME? COLON NCNAME_EXT
  ;

STRING_WITH_QUOTE
  : '\'' ( options {greedy=false  ;} : ~('\u0027' | '\u005C' | '\u000A' | '\u000D') | ECHAR )* '\''
  ;

STRING_WITH_QUOTE_DOUBLE
  : '"'  ( options {greedy=false  ;} : ~('\u0022' | '\u005C' | '\u000A' | '\u000D') | ECHAR )* '"'
  ;

STRING_WITH_BRACKET
  : '<' ( options {greedy=false  ;} : ~('\u0022' | '\u005C' | '\u000A' | '\u000D') | ECHAR )* '>'
  ;

STRING_WITH_CURLY_BRACKET
  : '{' ( options {greedy=false  ;} : ~('\u0022' | '\u005C' | '\u000A' | '\u000D') | ECHAR )* '}'
  ;

STRING_URI
  : SCHEMA COLON DOUBLE_SLASH (URI_PATH)*
  ;
  
WS: (' '|'\t'|('\n'|'\r'('\n')))+ {$channel=HIDDEN;};
  