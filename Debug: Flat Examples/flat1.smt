----------------------------OCA  INPUT----------------------------
InitState: 0
TargetState: 3
State: 0 
(0,add,1)
State: 1 
(1,sub,2)
(1,sub,3)
State: 2 
(2,sub,1)
State: 3 
(3,sub,3)

------------------------------------------------------------------
--------------------------FORMULA OUTPUT--------------------------
(declare-fun xs () Int)
(declare-fun xt () Int)


(let ((a!1 (exists ((v_o_3 Int) (v_i_2 Int) (v_o_2 Int) (v_i_1 Int))
             (let ((a!1 (or (and (= (- v_i_2 v_o_3) 1) (= v_o_3 xs)) false))
                   (a!2 (or (= v_i_2 v_o_2)
                            (and (= v_o_2 (+ v_i_2 (- 2)))
                                 (>= (+ v_i_2 (- 2)) 0))
                            (and (= v_o_2 (+ v_i_2 (- 4)))
                                 (>= (+ v_i_2 (- 4)) 0))
                            (and (= v_o_2 (+ v_i_2 (- 6)))
                                 (>= (+ v_i_2 (- 6)) 0))))
                   (a!3 (and (= (- v_i_2 v_o_3) 1) (= (- v_i_1 v_o_2) (- 1))))
                   (a!4 (exists ((z2 Int))
                          (let ((a!1 (exists ((z1 Int))
                                       (let ((a!1 (exists ((f_1_2 Int)
                                                           (f_2_1 Int))
                                                    (and (= (+ f_1_2 0)
                                                            (+ f_2_1 0))
                                                         (= (+ f_2_1 0)
                                                            (+ f_1_2 0))
                                                         (= (+ f_2_1 0)
                                                            (+ f_1_2 0))
                                                         (= (+ 0
                                                               (* (- 1) f_1_2)
                                                               (* (- 1) f_2_1))
                                                            (- z1 v_i_2))
                                                         (and true
                                                              (>= f_1_2 0)
                                                              (>= f_2_1 0)))))
                                             (a!2 (or (= z1 v_o_2)
                                                      (and (= v_o_2
                                                              (+ z1 (- 2)))
                                                           (>= (+ z1 (- 2)) 0))
                                                      (and (= v_o_2
--------------------------FORMULA OUTPUT--------------------------
                                                              (+ z1 (- 4)))
                                                           (>= (+ z1 (- 4)) 0))
                                                      (and (= v_o_2
                                                              (+ z1 (- 6)))
                                                           (>= (+ z1 (- 6)) 0)))))
                                         (and (>= z1 0)
                                              (or false (and a!1 (>= z1 2) a!2))
                                              (= (- v_i_2 v_o_3) 1)
                                              (= (- v_i_1 v_o_2) (- 1))))))
                                (a!2 (exists ((f_1_2 Int) (f_2_1 Int))
                                       (and (= (+ (+ f_2_1 0) 1) (+ f_1_2 0))
                                            (= (+ f_1_2 0) (+ (+ f_2_1 0) 1))
                                            (= (+ 0
                                                  (* (- 1) f_1_2)
                                                  (* (- 1) f_2_1))
                                               (- z2 v_i_2))
                                            (and true (>= f_1_2 0) (>= f_2_1 0)))))
                                (a!3 (or false
                                         (and (= v_o_2 (+ z2 (- 1)))
                                              (>= (+ z2 (- 1)) 0))
                                         (and (= v_o_2 (+ z2 (- 3)))
                                              (>= (+ z2 (- 3)) 0))
                                         (and (= v_o_2 (+ z2 (- 5)))
                                              (>= (+ z2 (- 5)) 0))
                                         (and (= v_o_2 (+ z2 (- 7)))
                                              (>= (+ z2 (- 7)) 0))
                                         (and (= v_o_2 (+ z2 (- 9)))
                                              (>= (+ z2 (- 9)) 0)))))
                            (and (>= z2 0)
                                 (or a!1 (and a!2 (>= z2 2) a!3))
                                 (= (- v_i_2 v_o_3) 1)
                                 (= (- v_i_1 v_o_2) (- 1))))))
                   (a!6 (or (= v_i_1 xt)
                            (and (= xt (+ v_i_1 (- 1))) (>= (+ v_i_1 (- 1)) 0))
                            (and (= xt (+ v_i_1 (- 2))) (>= (+ v_i_1 (- 2)) 0))
                            (and (= xt (+ v_i_1 (- 3))) (>= (+ v_i_1 (- 3)) 0))
                            (and (= xt (+ v_i_1 (- 4))) (>= (+ v_i_1 (- 4)) 0))))
                   (a!7 (exists ((z3 Int))
                          (let ((a!1 (exists ((f_3_3 Int))
                                       (and (= (+ f_3_3 0) (+ f_3_3 0))
                                            (= (+ f_3_3 0) (+ f_3_3 0))
                                            (= (+ 0 (* (- 1) f_3_3))
                                               (- z3 v_i_1))
                                            true
                                            (>= f_3_3 0))))
                                (a!2 (or (= z3 xt)
                                         (and (= xt (+ z3 (- 1)))
                                              (>= (+ z3 (- 1)) 0))
                                         (and (= xt (+ z3 (- 2)))
                                              (>= (+ z3 (- 2)) 0))
                                         (and (= xt (+ z3 (- 3)))
                                              (>= (+ z3 (- 3)) 0))
                                         (and (= xt (+ z3 (- 4)))
                                              (>= (+ z3 (- 4)) 0)))))
                            (and (>= z3 0)
                                 (or false (and a!1 (>= z3 1) a!2))
                                 (= (- v_i_1 v_o_2) (- 1)))))))
             (let ((a!5 (or (and a!2 a!3)
                            a!4
                            (and a!3 (or false (= v_i_2 v_o_2)))
                            (and a!3 (or false (= v_i_2 v_o_2)))
                            (and a!3 (or false (= v_i_2 v_o_2)))
                            false))
                   (a!8 (or (and a!6 (= (- v_i_1 v_o_2) (- 1)))
                            a!7
                            (and (= (- v_i_1 v_o_2) (- 1))
                                 (or false (= v_i_1 xt)))
                            false)))
               (and (or false (and true a!1 a!5 a!8))
                    true
                    (>= v_o_3 0)
                    (>= v_i_2 0)
                    (>= v_o_2 0)
                    (>= v_i_1 0))))))
      (a!3 (exists ((i Int) (j Int)) (and (= xt (+ xs (* (- 1) j) (* (- 2) i) 0))
                (>= j 0)
                (>= i 0)
                (>= xt 0)
                (>= xs 0)))))
(let ((a!2 (and (or false a!1 false false) (>= xs 0) (>= xt 0))))
  (not (and (=> a!2 a!3) (=> a!3 a!2)))))

------------------------------------------------------------------