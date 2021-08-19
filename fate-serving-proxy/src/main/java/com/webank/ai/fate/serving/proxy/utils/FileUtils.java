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

package com.webank.ai.fate.serving.proxy.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static String fileMd5(String filePath) {
        InputStream in = null;
        try {
            in = new FileInputStream(filePath);
            return DigestUtils.md5DigestAsHex(in);
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return null;
    }

    public static boolean writeFile(String context, File target) {
        BufferedWriter out = null;
        try {
            if (!target.exists()) {
                target.createNewFile();
            }
            out = new BufferedWriter(new FileWriter(target));
            out.write(context);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException ex) {

            }
        }
        return true;
    }

    /**
     * 将str写入文件,同步操作,独占锁
     */
    public static boolean writeStr2ReplaceFileSync(String str, String pathFile) throws Exception {
        File file = new File(pathFile);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            logger.error("Failed to create the file. Check whether the path is valid and the read/write permission is correct");
            throw new IOException("Failed to create the file. Check whether the path is valid and the read/write permission is correct");
        }
        FileOutputStream fileOutputStream = null;
        FileChannel fileChannel = null;
        FileLock fileLock;//文件锁
        try {

            /**
             * 写文件
             */
            fileOutputStream = new FileOutputStream(file);
            fileChannel = fileOutputStream.getChannel();

            try {
                fileLock = fileChannel.tryLock();//独占锁
            } catch (Exception e) {
                logger.info("another thread is writing ,refresh and try again");
                throw new IOException("another thread is writing ,refresh and try again");
            }
            if (fileLock != null) {
                fileChannel.write(ByteBuffer.wrap(str.getBytes()));
                if (fileLock.isValid()) {
                    logger.info("release-write-lock");
                    fileLock.release();
                }
                if (file.length() != str.getBytes().length) {
                    logger.error("write successfully but the content was lost, reedit and try again");
                    throw new IOException("write successfully but the content was lost, reedit and try again");
                }
            }

        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } finally {
            close(fileChannel);
            close(fileOutputStream);
        }
        return true;
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
