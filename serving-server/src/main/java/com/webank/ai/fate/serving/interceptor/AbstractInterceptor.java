/*
 * Copyright 2019 The FATE Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.ai.fate.serving.interceptor;

import com.webank.ai.fate.serving.core.rpc.core.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * @Description TODO
 * @Author kaideng
 **/
public class AbstractInterceptor implements Interceptor, EnvironmentAware {

    protected Environment environment;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


}
