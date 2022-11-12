package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.parser.KeyWordsSet;
import com.alibaba.qlexpress4.parser.Token;

/**
 * Comment By 冰够 Precedence > OperatorPriority ?
 * <p>
 * Author: DQinYuan
 * date 2022/1/12 2:31 下午
 */
public class QLPrecedences {

    /**
     * = += -= &= |= *= /= %= <<= >>=
     */
    public static final int ASSIGN = 0;

    /**
     * ?:
     */
    public static final int TERNARY = 1;

    /**
     * || or
     */
    public static final int OR = 2;

    /**
     * && and
     */
    public static final int AND = 3;

    /**
     * |
     */
    public static final int BIT_OR = 4;

    /**
     * ^
     */
    public static final int XOR = 5;

    /**
     * &
     */
    public static final int BIT_AND = 6;

    /**
     * == !=
     */
    public static final int EQUAL = 7;

    /**
     * < <= > >= instanceof
     */
    public static final int COMPARE = 8;

    /**
     * << >> >>>
     */
    public static final int BIT_MOVE = 9;

    /**
     * in like
     */
    public static final int IN_LIKE = 10;

    /**
     * + -
     */
    public static final int ADD = 11;

    /**
     * * / %
     */
    public static final int MULTI = 12;

    /**
     * ! ++ -- ~ + -
     */
    public static final int UNARY = 13;

    /**
     * ++ -- in suffix, like i++
     */
    public static final int UNARY_SUFFIX = 14;

    /**
     * ()
     */
    public static final int GROUP = 15;

    public static Integer getMiddlePrecedence(Token token) {
        switch (token.getType()) {
            case OR:
                return OR;
            case AND:
                return AND;
            case BITOR:
                return BIT_OR;
            case CARET:
                return XOR;
            case BITAND:
                return BIT_AND;
            case EQUAL:
            case NOTEQUAL:
                return EQUAL;
            case LT:
            case LE:
            case GT:
            case GE:
                return COMPARE;
            case LSHIFT:
            case RSHIFT:
            case URSHIFT:
                return BIT_MOVE;
            case ADD:
            case SUB:
                return ADD;
            case MUL:
            case DIV:
            case MOD:
                return MULTI;
            case INC:
            case DEC:
                // suffix operator
                return UNARY_SUFFIX;
            case DOT:
                // field call
            case METHOD_REF:
                // method reference
            case LBRACK:
                // array index call
            case LPAREN:
                // method call
                return GROUP;
            // assign operators
            case ASSIGN:
            case ADD_ASSIGN:
            case SUB_ASSIGN:
            case AND_ASSIGN:
            case OR_ASSIGN:
            case MUL_ASSIGN:
            case MOD_ASSIGN:
            case LSHIFT_ASSIGN:
            case RSHIFT_ASSIGN:
            case URSHIFT_ASSIGN:
            case DIV_ASSIGN:
                return ASSIGN;
            case QUESTION:
                return TERNARY;
            case KEY_WORD:
                if (KeyWordsSet.OR.equals(token.getLexeme())) {
                    return OR;
                } else if (KeyWordsSet.AND.equals(token.getLexeme())) {
                    return AND;
                } else if (KeyWordsSet.INSTANCEOF.equals(token.getLexeme())) {
                    return COMPARE;
                } else if (KeyWordsSet.IN.equals(token.getLexeme()) ||
                        // TODO: like 的运行时性能优化
                        KeyWordsSet.LIKE.equals(token.getLexeme())) {
                    return IN_LIKE;
                }
            default:
                return null;
        }
    }

    public static Integer getPrefixPrecedence(Token token) {
        switch (token.getType()) {
            case ADD:
            case SUB:
            case BANG:
            case INC:
            case DEC:
            case TILDE:
                return UNARY;
            case LPAREN:
                return GROUP;
            default:
                return null;
        }
    }
}