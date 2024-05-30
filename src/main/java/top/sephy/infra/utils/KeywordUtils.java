package top.sephy.infra.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeywordUtils {

    @Data
    public static class KeywordTreeNode {

        private char character;

        private int deep;

        private boolean isEnd;

        private Map<Integer, KeywordTreeNode> children;

        public KeywordTreeNode(char character, int deep) {
            this.character = character;
            this.deep = deep;
            this.children = new HashMap<>();
        }
    }

    public static boolean matchKeywords(String textToSearch, Map<Integer, KeywordTreeNode> keywords) {
        int len = textToSearch.length();

        boolean match = false;

        char[] chars = textToSearch.toCharArray();

        for (int i = 0; i < len; i++) {

            StringBuilder sb = new StringBuilder();
            Map<Integer, KeywordTreeNode> tmpMap = null;
            KeywordTreeNode node = null;
            KeywordTreeNode parent = null;

            char first = chars[i];
            node = keywords.get((int)first);

            if (node != null) {
                sb.append(node.character);
                parent = node;
                int j = i + 1;

                for (; j < len; j++) {
                    char c = chars[j];
                    tmpMap = parent.getChildren();
                    node = tmpMap.get((int)c);

                    if (node == null) {
                        if (parent.isEnd) {
                            match = true;
                        }
                        break;
                    } else {
                        sb.append(c);
                        boolean isEnd = (j + 1 == len);
                        // 为了匹配最大长度关键字, 继续下一次循环
                        if (isEnd) {
                            match = node.isEnd;
                        }
                        parent = node;
                    }
                }
            }

            if (match) {
                log.debug("命中关键字: {}", sb);
                break;
            }
        }

        return match;
    }

    public static boolean matchKeywords(String textToSearch, Collection<String> keywords) {
        return matchKeywords(textToSearch, constructKeywordsTree(keywords));
    }

    public static Map<Integer, KeywordTreeNode> constructKeywordsTree(Collection<String> keywords) {

        Map<Integer, KeywordTreeNode> rootMap = new HashMap<>();

        for (String keyword : keywords) {

            char[] chars = keyword.toCharArray();
            int len = keyword.length();

            Map<Integer, KeywordTreeNode> tmpMap = null;
            KeywordTreeNode node = null;
            KeywordTreeNode parent = null;

            for (int i = 0; i < len; i++) {

                char c = chars[i];
                boolean isEnd = (i + 1 == len);

                if (parent != null) {
                    tmpMap = parent.getChildren();
                } else {
                    tmpMap = rootMap;
                }

                node = tmpMap.get((int)c);

                if (node == null) {
                    node = new KeywordTreeNode(c, i);
                    tmpMap.put((int)c, node);
                }

                if (!node.isEnd) {
                    node.setEnd(isEnd);
                }

                parent = node;
            }
        }

        return rootMap;
    }
}
