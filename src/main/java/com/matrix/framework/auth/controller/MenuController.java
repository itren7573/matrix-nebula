package com.matrix.framework.auth.controller;

import com.matrix.framework.auth.data.MenuPo;
import com.matrix.framework.auth.service.MenuService;
import com.matrix.framework.core.annotation.validation.LogCollector;
import com.matrix.framework.core.common.result.Result;
import com.matrix.framework.core.i18n.I18n;
import com.matrix.framework.core.i18n.MessageConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @LogCollector
    @GetMapping
    public Flux<MenuPo> list(ServerWebExchange request) {
        return menuService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<MenuPo>> getMenuById(@PathVariable Long id) {
        return menuService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @LogCollector
    @PostMapping("/save")
    public Mono<Result<Object>> save(@RequestBody MenuPo menuPo, ServerWebExchange request) {
        return menuService.save(menuPo)
                .map(d -> Result.ok())
                .onErrorResume(e -> {
                    if (e.getMessage().contains("idx_name")) {
                        return Mono.just(Result.checkFail(menuPo.getName() + I18n.getMessage(MessageConstants.RESPONSE_CHECK_EXIST)));
                    }
                    Result<Object> errResult = Result.fail();
                    errResult.setMessage(e.getMessage());
                    return Mono.just(errResult);
                });
    }

    @LogCollector
    @DeleteMapping("/delete")
    public Mono<Result<Object>> deleteMenu(@RequestParam("id") String id, ServerWebExchange request) {
        return menuService.delete(Long.parseLong(id))
                .then(Mono.just(Result.ok().message(I18n.getMessage(MessageConstants.DELETE_SUCCESS))));
    }
} 