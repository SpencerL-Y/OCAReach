----------------------------OCA  INPUT----------------------------
InitState: 0
TargetState: 3
State: 0 
(0,sub,1)
State: 1 
(1,sub,2)
(1,add,1)
State: 2 
(2,add,3)
(2,sub,2)
State: 3 

------------------------------------------------------------------
--------------------------FORMULA OUTPUT--------------------------
(let ((a!1 (or false
               (exists ((v_o_4 Int)
                        (v_i_3 Int)
                        (v_o_3 Int)
                        (v_i_2 Int)
                        (v_o_2 Int)
                        (v_i_1 Int))
                 (let ((a!1 (or (and (= (- v_i_3 v_o_4) (- 1)) (= v_o_4 xs))
                                false))
                       (a!2 (or (and (= (- v_i_3 v_o_4) (- 1))
                                     (= (- v_i_2 v_o_3) (- 1))
                                     (or false (= v_i_3 v_o_3)))
                                false))
                       (a!3 (or (= v_i_2 v_o_2)
                                (and (= v_o_2 (+ v_i_2 (- 1)))
                                     (>= (+ v_i_2 (- 1)) 0))
                                (and (= v_o_2 (+ v_i_2 (- 2)))
                                     (>= (+ v_i_2 (- 2)) 0))
                                (and (= v_o_2 (+ v_i_2 (- 3)))
                                     (>= (+ v_i_2 (- 3)) 0))
                                (and (= v_o_2 (+ v_i_2 (- 4)))
                                     (>= (+ v_i_2 (- 4)) 0))))
                       (a!4 (and (= (- v_i_2 v_o_3) (- 1))
                                 (= (- v_i_1 v_o_2) 1)))
                       (a!5 (exists ((z2 Int))
                              (let ((a!1 (exists ((f_2_2 Int))
                                           (let ((a!1 (and (= (+ f_2_2 0)
                                                              (+ f_2_2 0))
                                                           (= (+ f_2_2 0)
                                                              (+ f_2_2 0)))))
                                             (and a!1
                                                  (= (+ 0 (* (- 1) f_2_2))
                                                     (- z2 v_i_2))
                                                  (and true (>= f_2_2 0))))))
                                    (a!2 (or (= z2 v_o_2)
                                             (and (= v_o_2 (+ z2 (- 1)))
                                                  (>= (+ z2 (- 1)) 0))
                                             (and (= v_o_2 (+ z2 (- 2)))
                                                  (>= (+ z2 (- 2)) 0))
                                             (and (= v_o_2 (+ z2 (- 3)))
                                                  (>= (+ z2 (- 3)) 0))
                                             (and (= v_o_2 (+ z2 (- 4)))
                                                  (>= (+ z2 (- 4)) 0)))))
                                (and (>= z2 0)
                                     (or false (and a!1 (>= z2 1) a!2))
                                     (= (- v_i_2 v_o_3) (- 1))
                                     (= (- v_i_1 v_o_2) 1)))))
                       (a!7 (or (and (= (- v_i_1 v_o_2) 1) (= xt v_i_1)) false)))
                 (let ((a!6 (or (and a!4 (or false (= v_i_2 v_o_2))) false)))
                 (let ((a!8 (or false
                                (and true
                                     a!1
                                     a!2
                                     (or (and a!3 a!4) a!5 a!6)
                                     a!7))))
                   (and a!8
                        true
                        (>= v_o_4 0)
                        (>= v_i_3 0)
                        (>= v_o_3 0)
                        (>= v_i_2 0)
                        (>= v_o_2 0)
                        (>= v_i_1 0))))))
               (exists ((vt_1 Int) (vs_2 Int))
                 (let ((a!1 (exists ((v_o_1 Int)
                                     (v_i_2 Int)
                                     (v_o_2 Int)
                                     (v_i_3 Int))
                              (let ((a!1 (or (and (= (- v_i_2 v_o_1) (- 1))
                                                  (= v_o_1 xt))
                                             false))
                                    (a!2 (and (= (- v_i_2 v_o_1) (- 1))
                                              (= (- v_i_3 v_o_2) 1)))
                                    (a!4 (or (= v_i_3 vs_2)
                                             (and (= vs_2 (+ v_i_3 (- 1)))
                                                  (>= (+ v_i_3 (- 1)) 0))
                                             (and (= vs_2 (+ v_i_3 (- 2)))
                                                  (>= (+ v_i_3 (- 2)) 0))
                                             (and (= vs_2 (+ v_i_3 (- 3)))
                                                  (>= (+ v_i_3 (- 3)) 0))
                                             (and (= vs_2 (+ v_i_3 (- 4)))
                                                  (>= (+ v_i_3 (- 4)) 0))))
                                    (a!5 (exists ((z1 Int))
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
                                                                     (* (- 1)
                                                                        f_1_1))
                                                                  (- z1 v_i_3))
                                                               (and true
                                                                    (>= f_1_1 0))))))
                                                 (a!2 (or (= z1 vs_2)
                                                          (and (= vs_2
                                                                  (+ z1 (- 1)))
                                                               (>= (+ z1 (- 1))
                                                                   0))
                                                          (and (= vs_2
                                                                  (+ z1 (- 2)))
                                                               (>= (+ z1 (- 2))
                                                                   0))
                                                          (and (= vs_2
                                                                  (+ z1 (- 3)))
                                                               (>= (+ z1 (- 3))
                                                                   0))
                                                          (and (= vs_2
                                                                  (+ z1 (- 4)))
                                                               (>= (+ z1 (- 4))
                                                                   0)))))
                                             (and (>= z1 0)
                                                  (or false
                                                      (and a!1 (>= z1 1) a!2))
                                                  (= (- v_i_3 v_o_2) 1)))))
                                    (a!7 (and true
                                              (>= v_o_1 0)
                                              (>= v_i_2 0)
                                              (>= v_o_2 0)
                                              (>= v_i_3 0))))
                              (let ((a!3 (or (and a!2
                                                  (or false (= v_i_2 v_o_2)))
                                             false))
                                    (a!6 (or (and a!4 (= (- v_i_3 v_o_2) 1))
                                             a!5
                                             (and (= (- v_i_3 v_o_2) 1)
                                                  (or false (= v_i_3 vs_2)))
                                             false)))
                                (and (or false (and true a!1 a!3 a!6)) a!7))))))
                 (let ((a!2 (and (or false (and true (= vt_1 xs)))
                                 true
                                 a!1
                                 (= (+ vt_1 (- 1)) vs_2))))
                   (or false a!2 false))))
               (exists ((vt_1 Int) (vs_3 Int) (vt_3 Int) (vs_2 Int))
                 (let ((a!1 (exists ((f_2_2 Int) (f_1_1 Int) (f_1_2 Int))
                              (and (= (+ (+ f_1_1 0) 1) (+ f_1_2 (+ f_1_1 0)))
                                   (= (+ f_1_2 (+ f_2_2 0)) (+ (+ f_2_2 0) 1))
                                   (= (+ 0
                                         (* (- 1) f_2_2)
                                         (* 1 f_1_1)
                                         (* (- 1) f_1_2))
                                      (- vt_3 vs_3))
                                   true
                                   (>= f_2_2 0)
                                   (>= f_1_1 0)
                                   (>= f_1_2 0))))
                       (a!3 (and true
                                 (>= vt_1 0)
                                 (>= vs_3 0)
                                 (>= vt_3 0)
                                 (>= vs_2 0))))
                 (let ((a!2 (and (or false (and true (= vt_1 xs)))
                                 true
                                 (exists ((vm_3_s Int) (vm_3_e Int))
                                   (let ((a!1 (or false
                                                  false
                                                  (and (>= (+ vs_3 0) 0)
                                                       (= (+ vs_3 1) vm_3_s))))
                                         (a!2 (or false
                                                  (and (>= (+ vt_3 0) 0)
                                                       (= (+ vt_3 1) vm_3_e))
                                                  false)))
                                     (and a!1
                                          a!2
                                          true
                                          (>= vm_3_s 0)
                                          (>= vm_3_e 0))))
                                 a!1
                                 (or false (and true (= vs_2 xt)))
                                 true
                                 (= vs_3 (+ vt_1 (- 1)))
                                 (= vs_2 (+ vt_3 1)))))
                   (and (or a!2 false) a!3))))))
      (a!2 (and (>= xs 1) (>= xt 1) (>= xt 0))))
(let ((a!3 (and (=> (and a!1 (>= xs 0) (>= xt 0)) a!2)
                (=> a!2 (and a!1 (>= xs 0) (>= xt 0))))))
  (not a!3)))
------------------------------------------------------------------
