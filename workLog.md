# 工作日志
## 组会
### 第三次组会
- 时间：1月24日 
- 和吴志林老师进行电话会议
- 会议内容
  -  博士论文的讲解
  -  主要讨论了 定理 4.1.6
  -  该定理的证明存在问题，only if方向存在问题需要证明
  -  分情况证明定理：
     -  s 不等于 t
        -  s = v
        -  s != v, v = t
        -  s != v, v != t
  - 强连通的条件是否必要？
- 评价：达不到要求，在看文章的时候应该注意细节问题，不能完全相信证明，应该自己动手推导一遍。下周约时间进行第二次电话会议。

### 第四次组会
- 时间：2月1日
- 和吴志林老师进行电话会议
- 会议内容
  - 继续探讨定理4.1.6中的问题
  - 吴志林老师给出了证明
  - 定理及证明细节（见tex文件）



### 第五次组会
- 时间：2月27日
- 313会议室
- 进度汇报

- Edge decomposition definition
- 研究问题：这个理论真正的理解，为什么这么定义。
- 给定一个Path flow如何找到它的edge decomposition

# 工作记录

## 一月 18日 家中
尝试重新证明定理4.1.6

## 二月 21日 

现在已经清晰地东西：

问题：Given a one-counter automaton and the starting configuration and the target configuration, whether the tartget configuration is reachable from the starting configuration.

问题的复杂度：
复杂度下界：NP 通过规约已经证明
复杂度上界：NPC 文章给出了一个NP算法

现在的问题：

1、该算法具体包括哪几个子算法

- 二月 25日 工作日志

2、该算法如何运行

- 二月 25日 工作日志
ji

3、该算法和QFPA的关系

- 二月 25日 工作日志

4、能否用QFPA的工具解决（可达性问题，有无正环问题）

- 解决问题（4）QFPA Solver工具调研：
  - A.J.C. Bik and H.A.G. Wijshoff. Implementation of Fourier-Motzkin elimination. Technical Report 94-42, Dept. of Computer Science, Leiden University, 1994.
  - D. C. Cooper. Theorem proving in arithmetic without ultiplication. InMachine Intelligence,volume 7, pages 91–99, New York, 1972. American Elsevier.
  - Weak second-order arithmetic and finite automata.
Zeitschrift  fur mathematische Logik und Grundladen der Mathematik
, 6:66–92, 1960.

- 不能用解决QFPA的工具解决有无正环问题，因为定义变量的时候定义了 $d^0, \ldots d^n$
  
5、算法的复杂度分析

- 算法的流程及复杂度分析的调研见 二月25日的工作日志


6、NOPOSITIVECYCLE这个问题的算法

7、为什么最后reachability算法是三指数算法

- 解决问题（7）： Presburger Arithmetic的判定算法中使用的FM算法的最坏情况的复杂度是双指数的，而QFPA的可满足性需要解决的FM instances的个数是指数个数的，所以最后的复杂度是双指数的。
- 具体参考文章 $2^{2^{2^n}}$ 里面关于Cooper算法的复杂度的分析和证明。

8、Fourier Motzkin Elimination and its propositional view


## 二月 24 日  咖啡厅

Fourier Motzkin 算法在整数域上的投影问题，投影得到的图形可能不是凸多边形。
一下对文章
On Solving Presburger and Linear Arithmetic with SAT
中列举的例子进行验证

$\exists x_2. (0 \le 3x_2-x_1\le7 \wedge 1 \le x_1-2x_2\le 5)$

如果是平常的算法：

$0 \le 3x_2-x_1$ 可以得到 $x_1 \le 3x_2$ --- 1

$3x_2-x_1\le7$ 可以得到 $3x_2 \le 7 + x_1$ --- 2

$1 \le x_1-2x_2$ 可以得到 $2x_2 \le x_1 - 1$ --- 3

$x_1-2x_2\le 5$ 可以得 $x_1 - 5 \le 2x_2$ --- 4

1+3 2+4 最终得到文章的结果


对算法的疑问：如何计算出算法的复杂度是文章中说的那个。

阅读文章：
W. Pugh and D. Wonnacott. Experiences with constraint-based array dependence
analysis. In Principles and Practice of Constraint Programming, pages 312–325,
1994.

## 二月 25日  

之前读的SAT解决QFP的可满足性问题的文章主要的工作是把naive的解决方法通过对逻辑表达式的处理，尽可能的减少时间开销。

理清楚整个算法的流程：

On the complexity 文章已经给出 one-counter automata 的可达性问题的是NPcomplete的，并给出了一套理论框架如何将给定一个自动机和两个格局从而确定是否存在一条路径可达的问题转化为QFPA的可满足性问题。


The definition of QFPA definable

$\exists$的几何意义：投影

```关系不大忽略```

进一步调研 Fourier Motzkin elimination：
阅读文章 Combinitorial Properties of FME

问题：为什么关注Combinitorial性质 

```关系不大忽略```


更正认识：QFPA的判定算法有两个
一个是 Cooper's Algorithm

另一个是 Omega test

开始阅读文章：phan_dung

找到分析Cooper算法的复杂度的文章 2^2^2^n文章：

问题：
1、为什么需要两个无穷公式来表达原来的有变量x的QFP公式，而不是只用其中一个？（是否是有的QFP公式只有一种比较符号）

- 是的，两个公式是对称的
  

## 二月 26日 
今天的任务：
1. 看完Cooper's algorithm 和 Omega test
2. 做明天报告的slides

$2^{2^{2^n}}$那篇文章：

- 复杂度分析部分定理一的证明有一个问题：

  - 第328页第三步的分析：


- 时间复杂度的证明的问题：
  - 第329页的这句话：
An upper bound on the deterministic time required to test the validity of a sentence
in Presburger arithmetic will be dominated by, say, the square of the time required
to write out the largest Fk

## 二月 26日 晚上加班 教学楼
做明天报告的slides


## 二月 27日 

review and finding prolems:
Main problem: how to express the algorithm in the QFPA formula?
- Definition 14: type-1 reachability criteria.

## 二月 28日 

为什么需要定义 positive v-cycle template:
在positive v-cycle template的定义中，要求 $\pi_1, \pi_2, \pi_3$的长度都要遭$|G|$以内，这样可以将路径上可能出现的指数长度的正环找一个多项式的certificate，就是这个template。

定理4.1.7的公式如何写出？
问题：在公式里面能否加入节点？

### 下午 1:30 和吴老师进行讨论

- support-edge decom edge decom
问题：为什么 support-edge decomposition 是有限多个？

- positive v-cycle template问题：为什么v-cycle template 是有限多个？


总结：整个算法的结构

接下来的工作：如何优化这个算法降低复杂度，减少暴力枚举的方法？

```阶段性任务完成```

接下来该做的事情：根据那篇文章将整个算法的输入、输出、伪代码写出来。搞清楚一流的一些细节问题。基于前两步对算法进行优化以降低复杂度。

## 三月 1日 

浏览论文 omega test


整理 On the Complexity 中的算法：

根据定理 4.1.14，我们有
如果在 $T(\mathcal{A})$中有从$(q,n)\rightarrow^* (q',n')$,那么在$G_\mathcal{A}$中对应有一条从$q$ 到$q'$的路径$\pi=\pi_1\cdot\pi_2\cdot\pi_3$. 使得分别对应type 1 type 3 type 2 的 reachability certificate.

整篇文章的思想是将reachability 问题转化为判定QFPA的可满足性。

算法框架包含两个部分：
1. 将问题转化为QFPA的有限集合
2. 对集合内的每一个公式，利用Cooper算法或者OmegaTest判断其可满足性

其中第一个部分的输入是一个one-counter automata和两个configuration： $\mathcal{A}$ 和 $(q,n), (q',n')$,假设$\mathcal{A}$是non 0-test的，可以很自然的将$\mathcal{A}$ 转化为其对应的weighted graph $G_\mathcal{A}$.

公式4.5给出了type-1 reachability certificate的公式，得到该公式需要给定输入：
$G, F, s,t ,(F_i,v_i,v_i',e_i)_{i\in [m]}$.

问题：如何找到每个可能的 s-t support和其对应的support edge decomposition

- 每个可能的 s-t support   
```哪些s-t support 需要被考虑？```
  - support edge decomposition 对$e_i$的要求是总的并起来要是整个$F$. 所以$m = numOfEdges(F)$  
  - ```具体得到support edge decomposition 的算法?```
- 每对可能的$s,t$

s-t support require the connectivity of $F$.

公式4.7给出了type-3 certificate 对应的QFPA表示，该公式需要给定的输入：$G,F, s,t,l,l'$

- $G_\mathcal{A}$中每个可能的positive cycle template 
- $G_{\mathcal{A}}^{op}$ 中每个可能的positive cycle template
- 每对可能的$(s,t)$
- 每个可能的$F$



在最终的算法中 $s,t$已经固定，需要两个中间节点。


## 三月 4日 

关键是要抓住思路：把exponential长的路径映射到多项式长度的path flow上，再对path flow做讨论。

开始阅读文章 
The Effects of Bounding Syntactic Resources on Presburger LTL
企图寻找一些已有的关于 one counter automata的一些结论

一些想法：s-t support 和 certificate的数量能够被什么限制住？

路径的长度？counter的upper bound？自动机的flatness？

此外这些限制不能对算法的正确性产生影响。

## 三月 5日 
明天下午讨论

两个子算法：
1. 从support edge 找出 support edge decomposition
2. 找出 positive cycle template





## 三月 6日 讨论

strongly connected component.

把整个图分成SCC能够有效的分析。

## 三月 7日 

根据老师strongly component 的思路，已经过了一遍算法，发现之中的问题， positive cycle  template 的要求还是必要的，因为需要用到定义中对于 $\verb{drop}$ 的要求来使得$\pi_3$的counter值不会小于0.


需要考虑的问题：

1、将可达性问题分为$\pi_1\cdot \pi_2 \cdot \pi_3$三段，分别为type-1 type-3 type-2
2、吴老师的想法：SCC找到positive cycle template 
3、在positive  SCC中可能也有一开始的simple path
4、在$\pi_1$中可能经过positive SCC
5、在$\pi_2$中要注意找的是positive cycle template$\rho_1\cdot \rho_2 \cdot  \rho_3$，对他们的长度要求是$|G|$.


## 三月 8日

把以上的思路整理成文章的sketch



## 三月 11日

问题postive cycle  template的寻找

How to enumerate all the postive cycle template??

不需要找，交给QFPA处理。

## 三月 12日

为了要转到对应的QFPA上，如果按照现在的理论框架，需要讲type-1的rechability certificate细分，感觉上是可以证明的，目前已有的结论：
1. 对于原来的一个support $F$ 和 一个support edge decomposition $F_i$, 如果从SCC的层面上去考虑，我们只需要考虑同一个SCC里面的sub-support 的 sub-support edge dcomposition 的次序。对一个$SCC = (V,E,\mu)$来说，如果

2. SCC之间的边只走了一次也就是 flow 是1

3. 需要引入新的中间变量，来记录每一个SCC走完以后的权重

4. ```问题： 如何处理SCC之间的concrete edges 的不确定性，如果要暴力枚举如何写公式？``` 


5. ```问题：对于SCC里面的support edge decomposition 有什么其他的要求？比如inport和outport?``` 
6. ASWG中不存在环。


## 三月 13日

### 讨论

现在我的问题：

1. 在type-1那段上面，我们需要猜每个SCC是有环还是只是一条simple path，如果有环要看是不是正环。如果我们猜一个SCC只是经过了一个simple path在这个SCC内是否还能使用动态规划的那套来计算，因为动态规划的信息只记录了路径的长度集合S中并没有记录是否存在环。


2. 如果有环，按照上次的算法给出了一个bound，问题：长度在公式中如何表示？如果长度小于bound是否用原来的path flow的算法？如果长度大于bound是不是将路径分为三段，如何写出公式？


### 晚上讨论内容

1. 对于以上问题1确实不能直接用动态规划来做，对于SCC中应该先猜出路径的support在进行dynamic programming的计算

2. type-2确实用动态规划很好做

3. 如果存在权重为0的环在进行动态规划计算的过程中需要把对应的tuple去除


### 接下来要做的事情

写出对应的理论和伪代码,子问题：

- 动态规划的具体算法

- 



## 星期一需要准备的东西


1. 有效身份证原件、复印件
2. 应届学生证原件、复印件
3. 成绩单原件
4. 一寸近期免冠照片
5. 政审表
6. 考生个人简历和自述
7. 其他材料
8. 准备笔试


## 三月 19日

继续写draft

## 三月 20日

需要做的事情:

1. 准备复试相关材料
2. 复习复试笔试
3. 准备个人ppt和5分钟讲话
4. 准备中期报告
5. 准备draft
6. 照相


本科毕业论文／设计题目及主要内容：
论文题目：含有一个计数器的自动机的可达关系的计算
主要内容：
本题目主要研究含有一个计数器的自动机(one-counter automaton)的可达性关系的算法与实现的问题。该算法的输入是一个one-counter automaton和对应自动机上的初始格局和最终格局，可达性问题询问在该自动机上是否存在从初始格局到最终格局的合法运行。在 [1] 中已经有对该问题的NP-Complete的证明，但是该文章仅在理论上给出了证明，并没有将计算该问题的算法详细给出，算法还存在很多细节上的问题，并且部分定理的证明并不完整。
此外我们希望解决one-coutner automata可达性更一般的问题，对于给出两个自动机的运行格局，将其对应的计数器的值用变量表示，最后算法得到一个线性算数公式，该公式能够被满足当且仅当从初始格局到最终格局的合法运行，这个问题比一般的可达性关系问题更强。
毕业论文打算先将该算法的伪代码具体给出，然后对算法进行优化，最终实现该算法。

附件：
1. Algorithm Draft：目前将 [1] 中的算法和存在的问题整理后得到的算法的雏形
参考文献：
[1] Christoph Haase, On the Complexity of Model Checking Counter Automata, 2011

本人系中国科学院大学2015届计算机科学与技术专业本科生，目前已结束课程学习并在软件所进行实习和毕业设计，毕业论文题目及内容如上所述。
国科大本科计算机专业开设的必修课有离散数学、计算机科学导论、数字电路、C语言、数据结构、计算机组成原理、计算机体系结构、操作系统、人工智能、计算机网络和编译原理以及其对应的实验课程，专业必修课程重心偏向于代码能力、硬件设计和计算机体系结构相关内容。此外我还选修了机器学习、面向对象和软件测试与分析。在2018年上半年利用学校的给的名额，曾在慕尼黑工大进行过半年的交换学习，选修了Model Checking课程。

大学分配学业导师，本着对于形式逻辑的好感和有一定基础的德语，我选择了国重张立军老师作为我的本科导师。2016年暑期社会实践期间，张老师在组内进行了一些小项目，主要是前端可视化的相关开发工作,并且尝试用一周时间阅读了一篇论文。曾在大二学习期间周末抽出时间参加组内的讨论班讲文章并和组内同学讨论。由于对自动机理论和图论相关内容感兴趣，我跟随组内博士生进行了相关的调研。
我以[1]为起始点调研了自动机的机器学习的相关领域的研究现状。自动机学习的基本框架为老师和学生两个模块，通过不断的询问给定的串是否被接收和学到的自动机是否和目标等价，最终可以得到一个和目标自动机等价的自动机。Angluin在[1]中首次提出学习DFA的算法L*，之后又有相关文章将该工作扩展到NFA学习[2]、符号化自动机的学习[3]。 在[4]中Angluin将学习算法扩展到了更强的omega自动机上，李勇师兄又发表了利用FDFA(family of DFAs)学习Buechi自动机的文章[5]，并基于该文章实现了工具ROLL。在2017年暑假在软件所实习期间，我基于该工具实现了[6]中提到的I/O自动机的学习算法以及设计了ROLL的可视化界面[7]，在此基础上对自动机学习理论和概率模型的结合产生想法，并在交换期间前往[6]作者Prof. Vaandrager的组内进行了一天的访问。此外，由于对于DFA已经有基于树的数据结构的算法[8]，我曾尝试能否用树的数据结构设计学习NFA的算法，但因为对NFA不存在Myhill-Nerode定理以及并没有更多的时间研究NFA的bisimulation的相关问题，该题目没有能够继续做下去。除此之外，结合学习算法，我还调研了模型检验相关理论内容[9,10]。
目前我在软件所实习，在吴志林老师的指导下进行毕业设计，除了算法草稿给出的内容，之后计划将算法实现并进行实验。

我的研究兴趣主要集中在自动机理论、形式化方法、模型检验，如果有今后有在软件所读研的机会，我想以毕业设计为出发点，继续调研这一类自动机的模型检验的问题，并尝试在这个方向上做一些工作。此外，我也希望能够利用学习算法像[11]一样做一些应用上的研究和成果。




[1] D. Angluin, Learning Regular Sets from Queries and Counterexamples
[2] B. Bollig et al., Angluin-Style Learning of NFA*
[3] S. Drews and L. D’Antoni, Learning Symbolic Automata
[4] D. Angluin and D. Fisman, Learning Regular Omega Languages
[5] Y. Li et al., A Novel Learning Algorithm for Büchi Automata based on Family of DFAs and Classification Trees
[6] F. Vaandrager, Learning I/O Automata
[7] https://github.com/qq1024407215
[8] M. J. Kearns and U. V. Vazirani. An Introduction to Computational Learning Theory
[9] Katoen, Principle of Model Checking
[10] D. Peled et al.,  Black Box Checking
[11] F.W. Vaandrager. Model learning. Cover article Communications of the ACM 60(2): 86-95. February 2017. 


## 三月 22日

打算今天将整个算法的draft给完成


下午提出的三个问题：
1. 解决3n^2 + 1 的理解问题
2. 把前面的整个一段作为一个图用1.的方法解决
3. 动态规划不需要对每个子图都去做，只需要找出正环并找出所有破坏正环所有的可能的子图，在上面做动态规划。


## 三月 23-28日

准备和参加复试

## 三月 29日

周末需要解决的几个问题：

1. 回顾之前看过的文章
2. 将之前写的算法的草稿再看一遍，思考是否有优化的空间或者有无错误
3. 调研one-counter的相关工具、presburger的相关工具——需要再次调研之前看过的文章
4. 慢慢开始写论文，先写preliminary
5. 开始实现自己的工具: 图的相关结构定义完成，完成DFS的实现

## 四月 1日 2日

现在已经搭好基本的框架

接下来需要做的事情：
1. 调研 Z3的格式
2. 实现 QFPA的相关结构
3. 实现 QFPA公式到Z3的转换
4. 需要思考的问题
  - 如何从代码的运行中生成满足SMT2格式的公式
5. 需要实现FormulaGenerator
6. 需要


## 四月 2 日 晚上教学楼

Syntax:
  Lexicon :
    <Whitespace Chars>
    <Printable Chars> :=
    <Digits>          := 
    <Letters>         :=
    <Numerals>        := ..
    <Decimals>        := ...
    <Hexadecimals>    := #x(0~F)* | #x(0~f)*
    <Binaries>        := #b(0|1)*
    <String literals> := anysequence of chars from <printable chars> or <whitespace char> dilimited by <"> and <"> cannot occur within a <string literal>
    <Reserver words> := ...
    <Symbol> := <simple symbol> | <quoted symbol>
      <simple symbols> := any non-empty sequence of <letter> and <digit> and <chars: ~ ! @ $ % ^ & * _ - + = < > . ? /> that does not start with a <digit> and is not <reserved words>
      <quoted symbol> := any sequence of <whitespace chars> and <printable chars> that starts and ends with <char: |> and does not contain <char: |> or <char: \>
    <Keywords> := is a token of the form <:simple symbol>
  S-expression: non-parenthesis token or a (possibly empty) sequence of <S-expression>
  <spec_constant> := <numeral> | <decimal> | <hexadecimal> | <binary> | <string>
  <s_expre> := <spec_constant> | <symbol> | <keyword> | (<s_expre>*)
  Identifiers:
    <index> := <numeral> | <symbol>
    <identifier> := <symbol> | ( _ <symbol> <index>^+ )
  Attributes:
    <attribute_value> := <spec_constant> | <symbol> | (<s_expre>*)
    <attribute> := <keyword> | <keyword><attribute_value>
  Sorts:
    <sort> := <identifier> | (<identifier> <sort>^+)
  Terms and Formulas:
    <qual_identifier> := <identifier> | (as <identifier> <sort>)
    <var_binding> := (<symbol> <term>)
    <sorted_var> := (<symbol> <sort>)
    <pattern> := <symbol> | (<symbol> <symbol>^+)
    <match_case> := (<pattern> <term>)
    <term> := <spec_constant> 
            | <qual_identifier>
            | (<qual_identifier> <term>^+)       
            | (let (<var_binding>^+) <term>)
            | (forall (<sorted_var>^+) <term>)
            | (exists (<sorted_var>^+) <term>)
            | (match <term> (<match_case>^+))
            | (! <term> <attribute>^+)


 
  Variable binders:
    (forall ((x1 s1) (x2 s2) ... (xn sn)) \phi)
    ==
    (forall ((x1 s1)) (forall ((x2 s2)) ..... \phi))
  Let: 
    (let ((x1 t1) ... (xn tn)) t) == ...
  Match:
    