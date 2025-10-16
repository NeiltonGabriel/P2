package br.ufal.ic.p2.wepayu.Comandos;

import br.ufal.ic.p2.wepayu.Exception.NaoHaComandoADesfazerException;
import br.ufal.ic.p2.wepayu.Exception.NaoHaComandoARefazerException;

import java.util.Stack;

public class Pilhas {
    private final Stack<Comando> undoStack = new Stack<>();
    private final Stack<Comando> redoStack = new Stack<>();

    public void execute(Comando comando) {
        comando.execute();
        undoStack.push(comando);
        redoStack.clear();
    }

    public void undo() {
        if (undoStack.isEmpty()) {
            throw new NaoHaComandoADesfazerException();
        }
        Comando comando = undoStack.pop();
        comando.undo();
        redoStack.push(comando);
    }

    public void redo() {
        if (redoStack.isEmpty()) {
            throw new NaoHaComandoARefazerException();
        }
        Comando comando = redoStack.pop();
        comando.execute();
        undoStack.push(comando);
    }
}