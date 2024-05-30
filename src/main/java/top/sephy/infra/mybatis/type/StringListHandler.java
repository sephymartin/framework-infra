package top.sephy.infra.mybatis.type;

public class StringListHandler extends AbstractListHandler<String> {

    @Override
    String stringToElement(String value) {
        return value;
    }
}
