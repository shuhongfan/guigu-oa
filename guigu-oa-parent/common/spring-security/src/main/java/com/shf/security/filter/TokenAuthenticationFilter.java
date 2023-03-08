package com.shf.security.filter;

import com.alibaba.fastjson.JSON;
import com.shf.common.jwt.JwtHelper;
import com.shf.common.result.ResponseUtil;
import com.shf.common.result.Result;
import com.shf.common.result.ResultCodeEnum;
import com.shf.security.custom.LoginUserInfoHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 认证解析token过滤器
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private RedisTemplate redisTemplate;

    public TokenAuthenticationFilter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        logger.info("uri:"+request.getRequestURI());
        //如果是登录接口，直接放行
        if("/admin/system/index/login".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if(null != authentication) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } else {
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.LOGIN_ERROR));
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        // token置于header里
        String token = request.getHeader("token");
        logger.info("token:"+token);
        if (!StringUtils.isEmpty(token)) {
            String username = JwtHelper.getUsername(token);
            logger.info("username:"+username);

            //通过ThreadLocal记录当前登录人信息
            LoginUserInfoHelper.setUserId(JwtHelper.getUserId(token));
            LoginUserInfoHelper.setUsername(username);

            if (!StringUtils.isEmpty(username)) {
//                通过username从redis获取权限数据
                String authString = (String) redisTemplate.opsForValue().get(username);

//                把redis获取字符串权限数据转换成集合类型
                if (!StringUtils.isEmpty(authString)) {
                    List<Map> mapList = JSON.parseArray(authString, Map.class);

                    List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
                    mapList.forEach(map -> authorityList.add(new SimpleGrantedAuthority((String) map.get("authority"))));
                    return new UsernamePasswordAuthenticationToken(username, null, authorityList);
                } else {
                    return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                }
            }
        }
        return null;
    }
}
