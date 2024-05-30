package top.sephy.infra.mybatis.type;

public class LongListHandler extends AbstractListHandler<Long> {

    @Override
    Long stringToElement(String value) {
        return Long.parseLong(value);
    }
}
