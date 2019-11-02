package com.freeman.spring.data;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * tpl模板解析sql语句
 */
public class TplNamedTemplateResolver implements NamedTemplateResolver {

    private String encoding = "UTF-8";

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

	@Override
	public Iterator<Void> doInTemplateResource(Resource resource, final NamedTemplateCallback callback) throws Exception {
		InputStream inputStream = resource.getInputStream();
		final List<String> lines = IoUtil.readLines(inputStream, encoding, new ArrayList());
		return new Iterator<Void>() {
			String name;

			StringBuilder content = new StringBuilder();

			int index = 0;

			int total = lines.size();

			@Override
			public boolean hasNext() {
				return index < total;
			}

			@Override
			public Void next() {
				do {
					String line = lines.get(index);
					if (isNameLine(line)) {
						name = StrUtil.trim(StrUtil.removePrefix(line, "#@@#"));
					}
					else {
						line = StrUtil.trimToNull(line);
						if (line != null) {
							if (hasInlineComments(line)) {
								int indexOf = line.indexOf("###");
								if (indexOf > 0)
									content.append(line.substring(0, indexOf)).append(" ");
							} else {
								content.append(line).append(" ");
							}
						}
					}
					index++;
				} while (!isLastLine() && !isNextNameLine());

				//next template
				if (null != name) {
					callback.process(name, content.toString());
				}
				name = null;
				content = new StringBuilder();
				return null;
			}

			@Override
			public void remove() {
				//ignore
			}

			private boolean isNameLine(String line) {
				return StrUtil.containsIgnoreCase(line, "#@@#");
			}
			private boolean hasInlineComments(String line) {
				return StrUtil.containsIgnoreCase(line, "###");
			}

			private boolean isNextNameLine() {
				String line = lines.get(index);
				return isNameLine(line);
			}

			private boolean isLastLine() {
				return index == total;
			}
		};
	}
}
