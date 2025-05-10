package com.matrix.framework.auth.service;

import com.matrix.framework.auth.data.MenuPo;
import com.matrix.framework.auth.repositories.MenuRepository;
import com.matrix.framework.core.i18n.I18n;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.matrix.framework.core.i18n.MessageConstants;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    public Flux<MenuPo> findAll() {
        return menuRepository.findAll();
    }

    public Mono<MenuPo> findById(Long id) {
        return menuRepository.findById(id);
    }

    public Mono<MenuPo> save(MenuPo menu) {
        return menuRepository.save(menu);
    }

    public Mono<Void> delete(Long id) {
        return menuRepository.deleteById(id);
    }

    public Mono<MenuPo> getById(Long id) {
        return menuRepository.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException(I18n.getMessage(MessageConstants.MENU_NOT_EXISTS))));
    }
} 