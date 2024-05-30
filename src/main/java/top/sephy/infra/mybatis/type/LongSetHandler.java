package top.sephy.infra.mybatis.type;

public class LongSetHandler extends AbstractSetHandler<Long> {
    @Override
    Long stringToElement(String str) {
        return Long.parseLong(str);
    }
}
