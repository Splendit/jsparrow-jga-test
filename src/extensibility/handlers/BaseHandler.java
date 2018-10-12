/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package extensibility.handlers;

/**
 * Convenience base class which all handler implementations extend. This class
 * provides the common properties required by all MergeHandler implemenations.
 * 
 * @author jfischer
 */
public abstract class BaseHandler implements MergeHandler, Comparable<Object> {

	protected int priority;
	protected String xpath;
	protected MergeHandler[] children = {};
	protected String name;

	@Override
	public int getPriority() {
		return priority;
	}

	@Override
	public String getXPath() {
		return xpath;
	}

	@Override
	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public void setXPath(String xpath) {
		this.xpath = xpath;
	}

	@Override
	public int compareTo(Object arg0) {
		return Integer.valueOf(getPriority()).compareTo(Integer.valueOf(((MergeHandler) arg0).getPriority()));
	}

	@Override
	public MergeHandler[] getChildren() {
		return children;
	}

	@Override
	public void setChildren(MergeHandler[] children) {
		this.children = children;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

}
