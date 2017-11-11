# Tomato
-------
A gradle plugin for android project to use Aspectj, support java, kotlin. And Instant Run works as well.


### Usage

#### Add it to your project

``` groovy
	buildscript {
	  repositories {
	    jcenter()
	  }
	
	  dependencies {
	    classpath 'com.forufamily.gradle.plugin:tomato:1.0.2'
	  }
	}
```

#### Apply plugin
  
- In App
``` groovy
	apply plugin: 'com.android.application'
	apply plugin: 'tomato'
	
	...
	
	tomato {
		// just work on App
		debug true
		// The item 'android.local.jars' means excludes all local jars. Cause all the local JarInput name startWith it.
		excludedJars = ['android.local.jars']
		// see: http://www.eclipse.org/aspectj/doc/released/devguide/ajc-ref.html
		ajcArgs = [...]
	}
```

  - In Library
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
