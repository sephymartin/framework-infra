package top.sephy.infra.mybatis.type;

public class DoubleSetHandler extends AbstractSetHandler<Double> {

    @Override
    Double stringToElement(String str) {
        return Double.parseDouble(str);
    }
}
