Change Log
==========

Version 1.0.5 *(2018-02-01)*
----------------------------

>  *Fix issue: Solving the problem of the previous transform can not clean build directory after Tomato transform finished*

Version 1.0.5-alpha1 *(2017-12-14)*
----------------------------

>  *'excludedJars' uses regular expressions to match. '^.\*' means all jar file do not weave aspect.*
> 
>  *Fix issue: close the unclosed jar file*

Version 1.0.4 *(2017-11-23)*
----------------------------

> *Fix library project injection failure when it dependent on other library and using other library's annotations*

Version 1.0.3 *(2017-11-23)*
----------------------------

> *Use ClassReader instead of ClassLoader to solve the exception caused by static instance initialization when looking for aspects*
> 
> *Partial support for .aj files*

Version 1.0.2 *(2017-11-11)*
----------------------------

> *Fix: One mistake when copy directory. It was happend for incremental task.*
> 
> *Add extensions:* 
> - [excludedJars](README.md)
> - [ajcArgs](http://www.eclipse.org/aspectj/doc/released/devguide/ajc-ref.html)

Version 1.0.1 *(2017-11-04)*
----------------------------

> *Initial release.*
