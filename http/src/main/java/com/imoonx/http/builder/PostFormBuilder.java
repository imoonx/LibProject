package com.imoonx.http.builder;

import com.imoonx.http.request.PostFormRequest;
import com.imoonx.http.request.RequestCall;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 构建post 表单提交
 */
public class PostFormBuilder extends OkHttpRequestBuilder {

    private List<FileInput> files = new ArrayList<FileInput>();

    @Override
    public RequestCall build() {
        return new PostFormRequest(url, tag, params, headers, files).build();
    }

    /**
     * 上传单个文件
     *
     * @param name     服务端接收字段
     * @param filename 文件名称
     * @param file     上传的文件
     * @return PostFormBuilder.this
     */
    public PostFormBuilder addFile(String name, String filename, File file) {
        files.add(new FileInput(name, filename, file));
        return this;
    }

    /**
     * 上传多个文件
     *
     * @param name     服务端接收字段
     * @param filename 文件名称
     * @param fileList 文件集合
     * @return PostFormBuilder.this
     */
    public PostFormBuilder addMoreFile(String name, String filename, List<File> fileList) {
        for (int i = 0; i < fileList.size(); i++) {
//            String fileName1 = fileList.get(i).getName();
//            String suffix = fileName1.substring(fileName1.lastIndexOf("."));// 文件后缀
//            files.add(new FileInput(name, fileName1 + System.currentTimeMillis() + suffix, fileList.get(i)));
            files.add(new FileInput(name, fileList.get(i).getName(), fileList.get(i)));
        }
        return this;
    }

    public static class FileInput {
        public String key;
        public String filename;
        public File file;

        public FileInput(String name, String filename, File file) {
            this.key = name;
            this.filename = filename;
            this.file = file;
        }

        @Override
        public String toString() {
            return "FileInput{" + "key='" + key + '\'' + ", filename='" + filename + '\'' + ", file=" + file + '}';
        }
    }

    @Override
    public PostFormBuilder url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public PostFormBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public PostFormBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    @Override
    public PostFormBuilder addParams(String key, String val) {
        if (this.params == null) {
            params = new ConcurrentHashMap<>();
        }
        if (val == null)
            params.put(key, "");
        else
            params.put(key, val);
        return this;
    }

    @Override
    public PostFormBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public PostFormBuilder addHeader(String key, String val) {
        if (this.headers == null) {
            headers = new ConcurrentHashMap<>();
        }
        headers.put(key, val);
        return this;
    }
}
