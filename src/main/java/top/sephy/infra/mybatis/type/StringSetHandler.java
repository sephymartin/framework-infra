package top.sephy.infra.mybatis.type;

public class StringSetHandler extends AbstractSetHandler<String> {

    @Override
    String stringToElement(String str) {
        return str;
    }
}
