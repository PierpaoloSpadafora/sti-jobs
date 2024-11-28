package unical.demacs.rdm.config;

import org.modelmapper.ModelMapper;

import java.util.List;

public class ExtendedModelMapper extends ModelMapper {

    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source.stream()
                .map(element -> this.map(element, targetClass))
                .toList();
    }
}
