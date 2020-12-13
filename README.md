
## 使用方法
从远程依赖：

在根目录的build.gradle中加入
```gradle
allprojects {
    repositories {
		...
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```
在主项目app的build.gradle中依赖
```gradle
dependencies {
    ...
    implementation 'com.github.qqlixiong:android-pdfview:v1.0.0'
}
```
## About
**qqlixiong：** 本人喜欢尝试新的技术，以后发现有好用的东西，我将会在企业项目中实战，没有问题了就会把它引入到**android-pdfview**中，一直维护着这套框架，谢谢各位朋友的支持。如果觉得这套框架不错的话，麻烦点个 **star**，你的支持则是我前进的动力！

## License

    Copyright 2020 qqlixiong
 
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
 
        http://www.apache.org/licenses/LICENSE-2.0
 
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.