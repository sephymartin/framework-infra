package top.sephy.infra.mybatis.type;

public class DoubleListHandler extends AbstractListHandler<Double> {

    @Override
    Double stringToElement(String str) {
        return Double.parseDouble(str);
    }
}
