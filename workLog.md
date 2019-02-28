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
- 软件所313会议室
- 进度汇报

- Edge decomposition definition
- 研究问题：这个理论真正的理解，为什么这么定义。
- 给定一个Path flow如何找到它的edge decomposition

# 工作记录

## 一月 18日 家中
尝试重新证明定理4.1.6

## 二月 21日 软件所

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

## 二月 25日  软件所

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
  

## 二月 26日 软件所
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


## 二月 27日 软件所

review and finding prolems:
Main problem: how to express the algorithm in the QFPA formula?
- Definition 14: type-1 reachability criteria.

## 二月 28日 软件所

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