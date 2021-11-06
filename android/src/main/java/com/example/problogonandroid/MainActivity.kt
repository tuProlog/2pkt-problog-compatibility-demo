package com.example.problogonandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.probabilistic
import it.unibo.tuprolog.solve.probability
import it.unibo.tuprolog.solve.problog.PROBLOG_OPERATORS
import it.unibo.tuprolog.theory.parsing.ClausesParser

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val lblCentral = findViewById<TextView>(R.id.lblCentral)

        val clausesParser = ClausesParser.withOperators(PROBLOG_OPERATORS)

        val probabilisticTheory = clausesParser.parseTheory(
            """
            0.6::edge(1,2).
            0.1::edge(1,3).
            0.4::edge(2,5).
            0.3::edge(2,6).
            0.3::edge(3,4).
            0.8::edge(4,5).
            0.2::edge(5,6).
            
            path(X,Y) :- edge(X,Y).
            path(X,Y) :- edge(X,Z),Y \== Z,path(Z,Y).
            """
        )

        val problogSolver = Solver.problog.solverWithDefaultBuiltins(staticKb = probabilisticTheory)
        val goal = Struct.of("path", Var.of("From"), Var.of("To"))
        for (solution in problogSolver.solve(goal, SolveOptions.allLazily().probabilistic())) {
            if (solution.isYes) {
                lblCentral.append("yes: ${solution.solvedQuery} with probability ${solution.probability}\n")
            }
        }
    }
}