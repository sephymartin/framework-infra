package top.sephy.infra.mybatis.type;

public class IntegerListHandler extends AbstractListHandler<Integer> {

    @Override
    Integer stringToElement(String str) {
        return Integer.parseInt(str);
    }
}
