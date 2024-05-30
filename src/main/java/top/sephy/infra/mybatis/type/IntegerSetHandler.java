package top.sephy.infra.mybatis.type;

public class IntegerSetHandler extends AbstractSetHandler<Integer> {

    @Override
    Integer stringToElement(String str) {
        return Integer.parseInt(str);
    }
}
