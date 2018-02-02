# Tomato
-------
Tomato是一款在Android上使用切面注入框架(基于Aspectj)的插件
>- *支持 java、kotlin*
>- *支持 Instant Run*
>- *支持 .aj 文件编译(目前仅支持PointCut，Around，Before，After定义，其他支持开发中[主要是没时间])*

Tomato插件基于AspectJ。AspectJ支持java(.class文件)的注入，包括jar文件。所以Tomato也支持jar文件注入

### 产生的场景

在代码迁移到kotlin之前，我们使用大神JakeWharton在 [Hugo](https://github.com/JakeWharton/hugo) 中使用的方式进行切面注入。但是今年，我们将代码迁移到kotlin时发现原来的方式失效了。与此同时，我们也在github上发现 [aspectjx](https://github.com/HujiangTechnology/gradle_plugin_android_aspectjx) 可以工作。但在使用的过程中遇到一些问题，特别开启`Instant Run`功能时工作的不好。我们的项目现在编译时间已经很长了，每一次的全量编译让人无法忍受，所以我们希望`Instant Run`总是可以工作。基于以上这些原因，产生了这个项目

### 使用指南

**Gradle插件说明:** *Tomato工作在gradle插件版本3.0及以上版本，这个在我们的项目中进行过实践。gradle3.0之前的版本，不确定使用情况。目前暂时没有兼容之前版本的打算*

#### 引入插件

``` groovy
	buildscript {
	  repositories {
	    jcenter()
	  }
	
	  dependencies {
	    classpath 'com.forufamily.gradle.plugin:tomato:1.0.5'
	  }
	}
```

#### 使用插件
  
- Application中的使用
``` groovy
	apply plugin: 'com.android.application'
	apply plugin: 'tomato'
	
	...
	
	tomato {
		// debug模式开启和关闭只在Application中有效
		debug true
		// 为jar注入提供例外，这里使用正则表达式进行例外匹配. 如:'^.*'意味着任何jar都在例外中
		excludedJars = ['^.*']
		// 参数设置参见: http://www.eclipse.org/aspectj/doc/released/devguide/ajc-ref.html
		ajcArgs = [...]
	}
```

  - 库项目中的使用
``` groovy
	apply plugin: 'com.android.library'
	apply plugin: 'tomato'
	
	...
	
	tomato {
		excludedJars = [...]
		ajcArgs = [...]
	}
```

### License

```
	Copyright 2017 Junfeng Ren

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	   http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
```
