package edu.pw.automata.diagram

import edu.pw.automata.DFAService
import io.udash.wrappers.jquery._

object GraphService {

  val startingStyle = "[fillcolor = cyan, style=filled];"

  def repaint() = {

    jQ("#diagram-main").empty()

    val dfa = DFAService.dfa

    val accepting = dfa.getStates.filter(dfa.isAccepting(_))

    val s = new StringBuilder("digraph finite_state_machine {rankdir=LR;" +
      "node [shape = doublecircle];" + accepting.mkString(" ") + {if(accepting.isEmpty) ("") else (";")} +
	  "node [shape = point]; start;" + 
      "node [shape = circle];")
	  
	s.append(s"start -> ${DFAService.dfa.getQ0.get.toString};")
	
    dfa.getDelta.foreach(e => s.append(s"${e.a.toString} -> ${e.b.toString} [ label =" + "\"" + e.symbol.toString + "\"];"))

    if(Controller.current.isDefined || DFAService.dfa.getQ0.isDefined)
      s.append(s"${Controller.current.getOrElse(DFAService.dfa.getQ0.get).toString} $startingStyle")

    s.append("}")

    jQ("#diagram-main").append(DOMGlobalScope.drawGraph(s.toString()))
  }



}
