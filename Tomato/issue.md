## 1.避免切面中内联方法的产生

```java
@Aspect
public class DebuggerAspect {
    //...
   
    @Around("pointPrintLog()")
    public Object weavePrintJoinPoint(ProceedingJoinPoint point) throws Throwable {
        //...

        if (level > Debugger.NONE && level <= Debugger.A) {
             Log.println(PriorityConverter.convert(level), tag, msg);
        } else {
             System.out.println(msg);
        }
        //...
        return null;
    }

    @LogPriority
    public static int convert(int level) {
         switch (level) {
         case Debugger.I:
               return Log.INFO;
         case Debugger.D:
               return Log.DEBUG;
         case Debugger.W:
              return Log.WARN;
         case Debugger.E:
              return Log.ERROR;
         case Debugger.A:
              return Log.ASSERT;
         }
         return Log.VERBOSE;
     }

    @IntDef({Log.VERBOSE, Log.INFO, Log.DEBUG, Log.WARN, Log.ERROR, Log.ASSERT})
    @Retention(RetentionPolicy.SOURCE)
    @interface LogPriority {
    }
}

```

上述代码会产生内联方法
```java
public static int ajc$inlineAccessMethod$..._DebuggerAspect$priority(DebuggerAspect paramDebuggerAspect, int paramInt){
    return paramDebuggerAspect.priority(paramInt);
}
```

该内联方法会造成AJC在解析方法时抛出BCException