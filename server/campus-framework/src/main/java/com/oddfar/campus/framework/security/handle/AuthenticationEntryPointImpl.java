package com.oddfar.campus.framework.security.handle;

import com.alibaba.fastjson2.JSON;
import com.oddfar.campus.common.constant.HttpStatus;
import com.oddfar.campus.common.domain.R;
import com.oddfar.campus.common.utils.ServletUtils;
import com.oddfar.campus.common.utils.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * 认证失败处理类 返回未授权
 *
 * 
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {
    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException {
        int code = HttpStatus.UNAUTHORIZED;
        String msg = StringUtils.format("Request access: {}, authentication failed, unable to access system resources", request.getRequestURI());
        ServletUtils.renderString(response, JSON.toJSONString(R.error(code, msg)));
    }
}
