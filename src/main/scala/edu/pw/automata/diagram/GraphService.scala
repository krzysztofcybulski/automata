package edu.pw.automata.diagram

import edu.pw.automata.{DFAService, DOMGlobalScope}
import io.udash.wrappers.jquery._

object GraphService {


  def repaint() = {

    jQ("#diagram-main").empty()

    val dfa = DFAService.dfa

    val accepting = dfa.getStates.filter(dfa.isAccepting(_))

    val s = new StringBuilder("digraph finite_state_machine {" +
      "rankdir=LR;" +
      "size=\"8,5\"" +
      "node [shape = doublecircle];" + accepting.mkString(" ") + {if(accepting.isEmpty) ("") else (";")} +
      "node [shape = circle];")

    dfa.getDelta.foreach(e => s.append(e.a.toString + " -> " + e.b.toString + " [ label = \"" + e.symbol.toString() + "\" ];"))
    s.append("}")

    jQ("#diagram-main").append(DOMGlobalScope.drawGraph(s.toString()))
  }



}
