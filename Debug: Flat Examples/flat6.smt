----------------------------OCA  INPUT----------------------------
InitState: 0
TargetState: 2
State: 0 
(0,sub,1)
State: 1 
(1,sub,2)
(1,add,3)
State: 2 
State: 3 
(3,add,1)

------------------------------------------------------------------
--------------------------FORMULA OUTPUT--------------------------
(let ((a!1 (exists ((v_o_3 Int) (v_i_2 Int) (v_o_2 Int) (v_i_1 Int))
             (let ((a!1 (or (and (= (- v_i_2 v_o_3) (- 1)) (= v_o_3 xs)) false))
                   (a!2 (and (= (- v_i_2 v_o_3) (- 1))
                             (= (- v_i_1 v_o_2) (- 1))
                             (or false (= v_i_2 v_o_2))))
                   (a!3 (or (and (= (- v_i_1 v_o_2) (- 1)) (= xt v_i_1)) false)))
               (and (or false (and true a!1 (or a!2 a!2 a!2 false) a!3))
                    true
                    (>= v_o_3 0)
                    (>= v_i_2 0)
                    (>= v_o_2 0)
                    (>= v_i_1 0)))))
      (a!3 (exists ((i Int))
             (let ((a!1 (= xt (- (+ xs (* 2 i) 0) 2))))
               (and a!1 (>= i 0) (>= xt 0) (>= xs 0) (>= xs 1))))))
(let ((a!2 (and (or false
                    a!1
                    (exists ((vt_1 Int) (vs_2 Int))
                      (let ((a!1 (exists ((v_o_1 Int) (v_i_2 Int))
                                   (let ((a!1 (or (and (= (- v_i_2 v_o_1) 1)
                                                       (= v_o_1 xt))
                                                  false))
                                         (a!2 (or (= v_i_2 vs_2)
                                                  (and (= vs_2 (+ v_i_2 (- 2)))
                                                       (>= (+ v_i_2 (- 2)) 0))
                                                  (and (= vs_2 (+ v_i_2 (- 4)))
                                                       (>= (+ v_i_2 (- 4)) 0))
                                                  (and (= vs_2 (+ v_i_2 (- 6)))
                                                       (>= (+ v_i_2 (- 6)) 0))))
                                         (a!3 (exists ((z3 Int))
                                                (let ((a!1 (exists ((z1 Int))
                                                             (let ((a!1 (exists ((f_1_3 Int)
                                                                                 (f_3_1 Int))
                                                                          (and (= (+ f_1_3
                                                                                     0)
                                                                                  (+ f_3_1
                                                                                     0))
                                                                               (= (+ f_3_1
                                                                                     0)
                                                                                  (+ f_1_3
                                                                                     0))
                                                                               (= (+ f_3_1
                                                                                     0)
                                                                                  (+ f_1_3
                                                                                     0))
                                                                               (= (+ 0
                                                                                     (* (- 1)
                                                                                        f_1_3)
                                                                                     (* (- 1)
                                                                                        f_3_1))
                                                                                  (- z1
                                                                                     v_i_2))
                                                                               (and true
                                                                                    (>= f_1_3
                                                                                        0)
                                                                                    (>= f_3_1
                                                                                        0)))))
                                                                   (a!2 (or (= z1
                                                                               vs_2)
                                                                            (and (= vs_2
                                                                                    (+ z1
                                                                                       (- 2)))
                                                                                 (>= (+ z1
                                                                                        (- 2))
                                                                                     0))
                                                                            (and (= vs_2
                                                                                    (+ z1
                                                                                       (- 4)))
                                                                                 (>= (+ z1
                                                                                        (- 4))
                                                                                     0))
                                                                            (and (= vs_2
                                                                                    (+ z1
                                                                                       (- 6)))
                                                                                 (>= (+ z1
                                                                                        (- 6))
                                                                                     0)))))
                                                               (and (>= z1 0)
                                                                    (or false
                                                                        (and a!1
                                                                             (>= z1
                                                                                 2)
                                                                             a!2))
                                                                    (= (- v_i_2
                                                                          v_o_1)
                                                                       1)))))
                                                      (a!2 (exists ((f_1_3 Int)
                                                                    (f_3_1 Int))
                                                             (and (= (+ (+ f_3_1
                                                                           0)
                                                                        1)
                                                                     (+ f_1_3 0))
                                                                  (= (+ f_1_3 0)
                                                                     (+ (+ f_3_1
                                                                           0)
                                                                        1))
                                                                  (= (+ 0
                                                                        (* (- 1)
                                                                           f_1_3)
                                                                        (* (- 1)
                                                                           f_3_1))
                                                                     (- z3
                                                                        v_i_2))
                                                                  (and true
                                                                       (>= f_1_3
                                                                           0)
                                                                       (>= f_3_1
                                                                           0)))))
                                                      (a!3 (or false
                                                               (and (= vs_2
                                                                       (+ z3
                                                                          (- 1)))
                                                                    (>= (+ z3
                                                                           (- 1))
                                                                        0))
                                                               (and (= vs_2
                                                                       (+ z3
                                                                          (- 3)))
                                                                    (>= (+ z3
                                                                           (- 3))
                                                                        0))
                                                               (and (= vs_2
                                                                       (+ z3
                                                                          (- 5)))
                                                                    (>= (+ z3
                                                                           (- 5))
                                                                        0))
                                                               (and (= vs_2
                                                                       (+ z3
                                                                          (- 7)))
                                                                    (>= (+ z3
                                                                           (- 7))
                                                                        0))
                                                               (and (= vs_2
                                                                       (+ z3
                                                                          (- 9)))
                                                                    (>= (+ z3
                                                                           (- 9))
                                                                        0)))))
                                                  (and (>= z3 0)
                                                       (or a!1
                                                           (and a!2
                                                                (>= z3 2)
                                                                a!3))
                                                       (= (- v_i_2 v_o_1) 1)))))
                                         (a!4 (and (= (- v_i_2 v_o_1) 1)
                                                   (or false (= v_i_2 vs_2)))))
                                   (let ((a!5 (or (and a!2
                                                       (= (- v_i_2 v_o_1) 1))
                                                  a!3
                                                  a!4
                                                  a!4
                                                  a!4
                                                  false)))
                                     (and (or false (and true a!1 a!5))
                                          (and true (>= v_o_1 0) (>= v_i_2 0))))))))
                      (let ((a!2 (and (or false (and true (= vt_1 xs)))
                                      true
                                      a!1
                                      (= (+ vt_1 (- 1)) vs_2))))
                        (or false a!2 false))))
                    false)
                (>= xs 0)
                (>= xt 0))))
  (not (and (=> a!2 a!3) (=> a!3 a!2)))))
------------------------------------------------------------------
