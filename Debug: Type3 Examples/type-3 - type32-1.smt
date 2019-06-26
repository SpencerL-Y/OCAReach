----------------------------OCA  INPUT----------------------------
InitState: 0
TargetState: 2
State: 0 
(0,sub,1)
(0,add,3)
State: 1 
(1,add,2)
(1,sub,1)
State: 2 
(2,add,2)
State: 3 
(3,add,0)

------------------------------------------------------------------
--------------------------FORMULA OUTPUT--------------------------
(let ((a!1 (exists ((vt_1 Int) (vs_2 Int))
             (let ((a!1 (exists ((v_o_1 Int)
                                 (v_i_2 Int)
                                 (v_o_2 Int)
                                 (v_i_3 Int))
                          (let ((a!1 (or (= xt v_o_1)
                                         (and (= v_o_1 (+ xt (- 1)))
                                              (>= (+ xt (- 1)) 0))
                                         (and (= v_o_1 (+ xt (- 2)))
                                              (>= (+ xt (- 2)) 0))
                                         (and (= v_o_1 (+ xt (- 3)))
                                              (>= (+ xt (- 3)) 0))
                                         (and (= v_o_1 (+ xt (- 4)))
                                              (>= (+ xt (- 4)) 0))))
                                (a!2 (exists ((z2 Int))
                                       (let ((a!1 (exists ((f_2_2 Int))
                                                    (let ((a!1 (and (= (+ f_2_2
                                                                          0)
                                                                       (+ f_2_2
                                                                          0))
                                                                    (= (+ f_2_2
                                                                          0)
                                                                       (+ f_2_2
                                                                          0)))))
                                                      (and a!1
                                                           (= (+ 0
                                                                 (* (- 1) f_2_2))
                                                              (- z2 xt))
                                                           (and true
                                                                (>= f_2_2 0))))))
                                             (a!2 (or (= z2 v_o_1)
                                                      (and (= v_o_1
                                                              (+ z2 (- 1)))
                                                           (>= (+ z2 (- 1)) 0))
                                                      (and (= v_o_1
                                                              (+ z2 (- 2)))
                                                           (>= (+ z2 (- 2)) 0))
                                                      (and (= v_o_1
                                                              (+ z2 (- 3)))
                                                           (>= (+ z2 (- 3)) 0))
                                                      (and (= v_o_1
                                                              (+ z2 (- 4)))
                                                           (>= (+ z2 (- 4)) 0)))))
                                         (and (>= z2 0)
                                              (or false (and a!1 (>= z2 1) a!2))
                                              (= (- v_i_2 v_o_1) (- 1))))))
                                (a!4 (and (= (- v_i_2 v_o_1) (- 1))
                                          (= (- v_i_3 v_o_2) 1)))
                                (a!6 (or (= v_i_3 xs)
                                         (and (= xs (+ v_i_3 (- 2)))
                                              (>= (+ v_i_3 (- 2)) 0))
                                         (and (= xs (+ v_i_3 (- 4)))
                                              (>= (+ v_i_3 (- 4)) 0))
                                         (and (= xs (+ v_i_3 (- 6)))
                                              (>= (+ v_i_3 (- 6)) 0))))
                                (a!7 (exists ((z3 Int))
                                       (let ((a!1 (exists ((z0 Int))
                                                    (let ((a!1 (exists ((f_0_3 Int)
                                                                        (f_3_0 Int))
                                                                 (and (= (+ f_0_3
                                                                            0)
                                                                         (+ f_3_0
                                                                            0))
                                                                      (= (+ f_3_0
                                                                            0)
                                                                         (+ f_0_3
                                                                            0))
                                                                      (= (+ f_3_0
                                                                            0)
                                                                         (+ f_0_3
                                                                            0))
                                                                      (= (+ 0
                                                                            (* (- 1)
                                                                               f_0_3)
                                                                            (* (- 1)
                                                                               f_3_0))
                                                                         (- z0
                                                                            v_i_3))
                                                                      (and true
                                                                           (>= f_0_3
                                                                               0)
                                                                           (>= f_3_0
                                                                               0)))))
                                                          (a!2 (or (= z0 xs)
                                                                   (and (= xs
                                                                           (+ z0
                                                                              (- 2)))
                                                                        (>= (+ z0
                                                                               (- 2))
                                                                            0))
                                                                   (and (= xs
                                                                           (+ z0
                                                                              (- 4)))
                                                                        (>= (+ z0
                                                                               (- 4))
                                                                            0))
                                                                   (and (= xs
                                                                           (+ z0
                                                                              (- 6)))
                                                                        (>= (+ z0
                                                                               (- 6))
                                                                            0)))))
                                                      (and (>= z0 0)
                                                           (or false
                                                               (and a!1
                                                                    (>= z0 2)
                                                                    a!2))
                                                           (= (- v_i_3 v_o_2) 1)))))
                                             (a!2 (exists ((f_0_3 Int)
                                                           (f_3_0 Int))
                                                    (and (= (+ (+ f_3_0 0) 1)
                                                            (+ f_0_3 0))
                                                         (= (+ f_0_3 0)
                                                            (+ (+ f_3_0 0) 1))
                                                         (= (+ 0
                                                               (* (- 1) f_0_3)
                                                               (* (- 1) f_3_0))
                                                            (- z3 v_i_3))
                                                         (and true
                                                              (>= f_0_3 0)
                                                              (>= f_3_0 0)))))
                                             (a!3 (or false
                                                      (and (= xs (+ z3 (- 1)))
                                                           (>= (+ z3 (- 1)) 0))
                                                      (and (= xs (+ z3 (- 3)))
                                                           (>= (+ z3 (- 3)) 0))
                                                      (and (= xs (+ z3 (- 5)))
                                                           (>= (+ z3 (- 5)) 0))
                                                      (and (= xs (+ z3 (- 7)))
                                                           (>= (+ z3 (- 7)) 0))
                                                      (and (= xs (+ z3 (- 9)))
                                                           (>= (+ z3 (- 9)) 0)))))
                                         (and (>= z3 0)
                                              (or a!1 (and a!2 (>= z3 2) a!3))
                                              (= (- v_i_3 v_o_2) 1)))))
                                (a!8 (and (= (- v_i_3 v_o_2) 1)
                                          (or false (= v_i_3 xs))))
                                (a!10 (and true
                                           (>= v_o_1 0)
                                           (>= v_i_2 0)
                                           (>= v_o_2 0)
                                           (>= v_i_3 0))))
                          (let ((a!3 (or (and a!1 (= (- v_i_2 v_o_1) (- 1)))
                                         a!2
                                         (and (= (- v_i_2 v_o_1) (- 1))
                                              (or false (= xt v_o_1)))
                                         false))
                                (a!5 (or (and a!4 (or false (= v_i_2 v_o_2)))
                                         false))
                                (a!9 (or (and a!6 (= (- v_i_3 v_o_2) 1))
                                         a!7
                                         a!8
                                         a!8
                                         a!8
                                         false)))
                            (and (or false (and true a!3 a!5 a!9)) a!10)))))
                   (a!2 (exists ((v_o_3 Int) (v_i_2 Int))
                          (let ((a!1 (and (= (- v_i_2 v_o_3) (- 1))
                                          (or false (= xs v_o_3))))
                                (a!2 (or (= v_i_2 vt_1)
                                         (and (= vt_1 (+ v_i_2 (- 1)))
                                              (>= (+ v_i_2 (- 1)) 0))
                                         (and (= vt_1 (+ v_i_2 (- 2)))
                                              (>= (+ v_i_2 (- 2)) 0))
                                         (and (= vt_1 (+ v_i_2 (- 3)))
                                              (>= (+ v_i_2 (- 3)) 0))
                                         (and (= vt_1 (+ v_i_2 (- 4)))
                                              (>= (+ v_i_2 (- 4)) 0))))
                                (a!3 (exists ((z1 Int))
                                       (let ((a!1 (exists ((f_1_1 Int))
                                                    (let ((a!1 (and (= (+ f_1_1
                                                                          0)
                                                                       (+ f_1_1
                                                                          0))
                                                                    (= (+ f_1_1
                                                                          0)
                                                                       (+ f_1_1
                                                                          0)))))
                                                      (and a!1
                                                           (= (+ 0
                                                                 (* (- 1) f_1_1))
                                                              (- z1 v_i_2))
                                                           (and true
                                                                (>= f_1_1 0))))))
                                             (a!2 (or (= z1 vt_1)
                                                      (and (= vt_1 (+ z1 (- 1)))
                                                           (>= (+ z1 (- 1)) 0))
                                                      (and (= vt_1 (+ z1 (- 2)))
                                                           (>= (+ z1 (- 2)) 0))
                                                      (and (= vt_1 (+ z1 (- 3)))
                                                           (>= (+ z1 (- 3)) 0))
                                                      (and (= vt_1 (+ z1 (- 4)))
                                                           (>= (+ z1 (- 4)) 0)))))
                                         (and (>= z1 0)
                                              (or false (and a!1 (>= z1 1) a!2))
                                              (= (- v_i_2 v_o_3) (- 1)))))))
                          (let ((a!4 (or (and a!2 (= (- v_i_2 v_o_3) (- 1)))
                                         a!3
                                         (and (= (- v_i_2 v_o_3) (- 1))
                                              (or false (= v_i_2 vt_1)))
                                         false)))
                            (and (or false
                                     (and true (or a!1 a!1 a!1 false) a!4))
                                 (and true (>= v_o_3 0) (>= v_i_2 0)))))))
                   (a!3 (or (= xt vs_2)
                            (and (= vs_2 (+ xt (- 1))) (>= (+ xt (- 1)) 0))
                            (and (= vs_2 (+ xt (- 2))) (>= (+ xt (- 2)) 0))
                            (and (= vs_2 (+ xt (- 3))) (>= (+ xt (- 3)) 0))
                            (and (= vs_2 (+ xt (- 4))) (>= (+ xt (- 4)) 0))))
                   (a!4 (exists ((z2 Int))
                          (let ((a!1 (exists ((f_2_2 Int))
                                       (let ((a!1 (and (= (+ f_2_2 0)
                                                          (+ f_2_2 0))
                                                       (= (+ f_2_2 0)
                                                          (+ f_2_2 0)))))
                                         (and a!1
                                              (= (+ 0 (* (- 1) f_2_2))
                                                 (- z2 xt))
                                              (and true (>= f_2_2 0))))))
                                (a!2 (or (= z2 vs_2)
                                         (and (= vs_2 (+ z2 (- 1)))
                                              (>= (+ z2 (- 1)) 0))
                                         (and (= vs_2 (+ z2 (- 2)))
                                              (>= (+ z2 (- 2)) 0))
                                         (and (= vs_2 (+ z2 (- 3)))
                                              (>= (+ z2 (- 3)) 0))
                                         (and (= vs_2 (+ z2 (- 4)))
                                              (>= (+ z2 (- 4)) 0)))))
                            (and (>= z2 0)
                                 (or false (and a!1 (>= z2 1) a!2))
                                 true)))))
             (let ((a!5 (or false
                            (and true (or false (= xt vs_2)))
                            (and a!3 true)
                            a!4)))
               (or a!1 (and a!2 (and a!5 true) (= (+ vt_1 1) vs_2)) false)))))
      (a!2 (exists ((vt_3 Int) (vs_2 Int))
             (let ((a!1 (exists ((vm_3_s Int) (vm_3_e Int))
                          (let ((a!1 (or false
                                         false
                                         (and (>= (+ xs 0) 0)
                                              (= (+ xs 2) vm_3_s))
                                         (and (>= (+ xs 0) 0)
                                              (= (+ xs 1) vm_3_s))))
                                (a!2 (or false
                                         (and (>= (+ vt_3 0) 0)
                                              (= (+ vt_3 1) vm_3_e))
                                         false
                                         false)))
                            (and a!1 a!2 (and true (>= vm_3_s 0) (>= vm_3_e 0))))))
                   (a!2 (exists ((f_1_1 Int)
                                 (f_0_1 Int)
                                 (f_0_3 Int)
                                 (f_3_0 Int))
                          (let ((a!1 (and true
                                          (>= f_1_1 0)
                                          (>= f_0_1 0)
                                          (>= f_0_3 0)
                                          (>= f_3_0 0))))
                            (and (= (+ f_0_3 0) (+ f_3_0 0))
                                 (= (+ (+ f_3_0 0) 1) (+ f_0_3 f_0_1 0))
                                 (= (+ f_0_1 (+ f_1_1 0)) (+ (+ f_1_1 0) 1))
                                 (= (+ 0
                                       (* (- 1) f_1_1)
                                       (* (- 1) f_0_1)
                                       (* 1 f_0_3)
                                       (* 1 f_3_0))
                                    (- vt_3 xs))
                                 a!1))))
                   (a!3 (or (= xt vs_2)
                            (and (= vs_2 (+ xt (- 1))) (>= (+ xt (- 1)) 0))
                            (and (= vs_2 (+ xt (- 2))) (>= (+ xt (- 2)) 0))
                            (and (= vs_2 (+ xt (- 3))) (>= (+ xt (- 3)) 0))
                            (and (= vs_2 (+ xt (- 4))) (>= (+ xt (- 4)) 0))))
                   (a!4 (exists ((z2 Int))
                          (let ((a!1 (exists ((f_2_2 Int))
                                       (let ((a!1 (and (= (+ f_2_2 0)
                                                          (+ f_2_2 0))
                                                       (= (+ f_2_2 0)
                                                          (+ f_2_2 0)))))
                                         (and a!1
                                              (= (+ 0 (* (- 1) f_2_2))
                                                 (- z2 xt))
                                              (and true (>= f_2_2 0))))))
                                (a!2 (or (= z2 vs_2)
                                         (and (= vs_2 (+ z2 (- 1)))
                                              (>= (+ z2 (- 1)) 0))
                                         (and (= vs_2 (+ z2 (- 2)))
                                              (>= (+ z2 (- 2)) 0))
                                         (and (= vs_2 (+ z2 (- 3)))
                                              (>= (+ z2 (- 3)) 0))
                                         (and (= vs_2 (+ z2 (- 4)))
                                              (>= (+ z2 (- 4)) 0)))))
                            (and (>= z2 0)
                                 (or false (and a!1 (>= z2 1) a!2))
                                 true)))))
             (let ((a!5 (or false
                            (and true (or false (= xt vs_2)))
                            (and a!3 true)
                            a!4)))
             (let ((a!6 (or (and a!1 a!2 (and a!5 true) (= vs_2 (+ vt_3 1)))
                            false)))
               (and a!6 (and true (>= vt_3 0) (>= vs_2 0)))))))))
(let ((a!3 (and (or false
                    (exists ((v_o_3 Int) (v_i_2 Int) (v_o_2 Int) (v_i_1 Int))
                      (let ((a!1 (and (= (- v_i_2 v_o_3) (- 1))
                                      (or false (= xs v_o_3))))
                            (a!2 (or (= v_i_2 v_o_2)
                                     (and (= v_o_2 (+ v_i_2 (- 1)))
                                          (>= (+ v_i_2 (- 1)) 0))
                                     (and (= v_o_2 (+ v_i_2 (- 2)))
                                          (>= (+ v_i_2 (- 2)) 0))
                                     (and (= v_o_2 (+ v_i_2 (- 3)))
                                          (>= (+ v_i_2 (- 3)) 0))
                                     (and (= v_o_2 (+ v_i_2 (- 4)))
                                          (>= (+ v_i_2 (- 4)) 0))))
                            (a!3 (and (= (- v_i_2 v_o_3) (- 1))
                                      (= (- v_i_1 v_o_2) 1)))
                            (a!4 (exists ((z1 Int))
                                   (let ((a!1 (exists ((f_1_1 Int))
                                                (let ((a!1 (and (= (+ f_1_1 0)
                                                                   (+ f_1_1 0))
                                                                (= (+ f_1_1 0)
                                                                   (+ f_1_1 0)))))
                                                  (and a!1
                                                       (= (+ 0 (* (- 1) f_1_1))
                                                          (- z1 v_i_2))
                                                       (and true (>= f_1_1 0))))))
                                         (a!2 (or (= z1 v_o_2)
                                                  (and (= v_o_2 (+ z1 (- 1)))
                                                       (>= (+ z1 (- 1)) 0))
                                                  (and (= v_o_2 (+ z1 (- 2)))
                                                       (>= (+ z1 (- 2)) 0))
                                                  (and (= v_o_2 (+ z1 (- 3)))
                                                       (>= (+ z1 (- 3)) 0))
                                                  (and (= v_o_2 (+ z1 (- 4)))
                                                       (>= (+ z1 (- 4)) 0)))))
                                     (and (>= z1 0)
                                          (or false (and a!1 (>= z1 1) a!2))
                                          (= (- v_i_2 v_o_3) (- 1))
                                          (= (- v_i_1 v_o_2) 1)))))
                            (a!6 (or (and (= (- v_i_1 v_o_2) 1)
                                          (or false (= v_i_1 xt)))
                                     false))
                            (a!8 (and true
                                      (>= v_o_3 0)
                                      (>= v_i_2 0)
                                      (>= v_o_2 0)
                                      (>= v_i_1 0))))
                      (let ((a!5 (or (and a!3 (or false (= v_i_2 v_o_2))) false)))
                      (let ((a!7 (or false
                                     (and true
                                          (or a!1 a!1 a!1 false)
                                          (or (and a!2 a!3) a!4 a!5)
                                          a!6))))
                        (and a!7 a!8)))))
                    a!1
                    a!2)
                (and (>= xs 0) (>= xt 0)))))
(let ((a!4 (and (=> a!3 (and (>= xs 0) (>= xt 1)))
                (=> (and (>= xs 0) (>= xt 1)) a!3))))
  (not a!4)))))
------------------------------------------------------------------
