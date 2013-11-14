(ns cornet.paths-test
  (:use [cornet paths]
        [clojure test]))



        


(deftest get-root-test
  (is (= (get-root "/a/b/c/") "/a/b/"))
  (is (= (get-root "/a/b/c") "/a/b/"))
  (is (= (get-root "/a/b/") "/a/"))
  (is (= (get-root "/abc") "/"))
  (is (= (get-root "/") nil)))

(deftest relative-filename-test
  (is (= (relative-filename "/a/b/c/" "../d.txt") "/a/b/d.txt"))
  (is (= (relative-filename "/a/b/c/" "d.txt") "/a/b/c/d.txt"))
  (is (= (relative-filename "/a/b/c/" "./d.txt") "/a/b/c/d.txt"))
  (is (= (relative-filename "/a/b/c/" "/d.txt") "/d.txt"))
  (is (= (relative-filename "/a/b/c/" "a1/d.txt") "/a/b/c/a1/d.txt"))
  )
