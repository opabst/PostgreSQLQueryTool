package de.oliverpabst.pqt.ui;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SqlHighlighter {

    private static final String[] KEYWORDS = {
        "ALL", "ALTER", "ANALYZE", "AND", "ARRAY", "AS", "ASC", "BEGIN",
        "BETWEEN", "BUFFERS", "BY", "CASE", "CAST", "CHECK", "CLUSTER",
        "COALESCE", "COMMIT", "CONFLICT", "CONSTRAINT", "COPY", "COSTS",
        "CREATE", "CROSS", "CURRENT", "DATABASE", "DEFAULT", "DELETE",
        "DESC", "DISTINCT", "DO", "DROP", "ELSE", "END", "EXCEPT", "EXISTS",
        "EXPLAIN", "EXTENSION", "FALSE", "FILTER", "FOLLOWING", "FOREIGN",
        "FORMAT", "FROM", "FULL", "FUNCTION", "GRANT", "GROUP", "HAVING",
        "IF", "ILIKE", "IN", "INDEX", "INNER", "INSERT", "INTERSECT",
        "INTO", "IS", "JOIN", "KEY", "LATERAL", "LEFT", "LIKE", "LIMIT",
        "NATURAL", "NOT", "NOTHING", "NULL", "NULLIF", "OFFSET", "ON",
        "OR", "ORDER", "OUTER", "OVER", "PARTITION", "PRECEDING", "PRIMARY",
        "PROCEDURE", "RANGE", "RECURSIVE", "REFERENCES", "REINDEX", "REPLACE",
        "RETURNING", "REVOKE", "RIGHT", "ROLE", "ROLLBACK", "ROW", "ROWS",
        "SAVEPOINT", "SCHEMA", "SELECT", "SEQUENCE", "SET", "SETOF",
        "TABLE", "TEMP", "TEMPORARY", "THEN", "TO", "TRIGGER", "TRUE",
        "TRUNCATE", "TYPE", "UNBOUNDED", "UNION", "UNIQUE", "UNLOGGED",
        "UPDATE", "USING", "VACUUM", "VALUES", "VARIADIC", "VERBOSE",
        "VIEW", "WHEN", "WHERE", "WINDOW", "WITH", "WITHIN"
    };

    private static final String KEYWORD_PATTERN =
            "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String STRING_PATTERN       = "'(?:[^']|'')*'";
    private static final String BLOCKCOMMENT_PATTERN = "/\\*.*?\\*/";
    private static final String LINECOMMENT_PATTERN  = "--[^\n]*";
    private static final String NUMBER_PATTERN       = "\\b\\d+(?:\\.\\d+)?\\b";
    private static final String OPERATOR_PATTERN     =
            "(<>|!=|->>|->|::|->=?|>=?|<=?|[+\\-*/%=!|&^~])";

    private static final Pattern PATTERN = Pattern.compile(
              "(?<LINECOMMENT>"  + LINECOMMENT_PATTERN  + ")"
            + "|(?<BLOCKCOMMENT>" + BLOCKCOMMENT_PATTERN + ")"
            + "|(?<STRING>"      + STRING_PATTERN       + ")"
            + "|(?<NUMBER>"      + NUMBER_PATTERN       + ")"
            + "|(?<KEYWORD>"     + KEYWORD_PATTERN      + ")"
            + "|(?<OPERATOR>"    + OPERATOR_PATTERN     + ")",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private SqlHighlighter() {}

    public static StyleSpans<Collection<String>> computeHighlighting(final String text) {
        final Matcher matcher = PATTERN.matcher(text);
        final StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        int lastEnd = 0;
        while (matcher.find()) {
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastEnd);
            spansBuilder.add(Collections.singleton(styleClassFor(matcher)),
                    matcher.end() - matcher.start());
            lastEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastEnd);
        return spansBuilder.create();
    }

    private static String styleClassFor(final Matcher matcher) {
        if (matcher.group("LINECOMMENT")  != null) return "linecomment";
        if (matcher.group("BLOCKCOMMENT") != null) return "blockcomment";
        if (matcher.group("STRING")       != null) return "string";
        if (matcher.group("NUMBER")       != null) return "number";
        if (matcher.group("KEYWORD")      != null) return "keyword";
        return "operator";
    }
}
