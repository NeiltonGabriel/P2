package br.ufal.ic.p2.wepayu.Actions;

import br.ufal.ic.p2.wepayu.models.Assalariado;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.Horista;

import java.time.LocalDate;
import java.time.DayOfWeek;

import static br.ufal.ic.p2.wepayu.Actions.Gerente.trabalhadores;
import static br.ufal.ic.p2.wepayu.Actions.Horarios.cartoes;

/**
 * Taxa total = taxaSindical * quantidade de dias do mês
 */
public class Pagamentos {

    public static double bruto(Empregado atual, String limite){

        String id = atual.getId();
        String comeco;
        LocalDate fim = Horarios.arrumar(limite);
        fim = fim.plusDays(1); //necessario pois os metodos horasNormais e horasExtras nao contabilizam o ultimo dia
        String[] partes = fim.toString().split("-");
        limite = partes[2].concat("/" + partes[1] + "/" + partes[0]);


        if (atual instanceof Horista){
            if (((Horista) atual).getDataContratacao().equals("-1")) return 0;
            else if (((Horista) atual).getUltimoPagamento().equals("-1")) comeco = ((Horista) atual).getDataContratacao();
            else comeco = ((Horista) atual).getUltimoPagamento();
            double horasNormais = 0, horasExtras = 0;
            horasNormais = Double.parseDouble(Horarios.horasNormais(id, comeco, limite));
            horasExtras = Double.parseDouble(Horarios.horasExtras(id, comeco, limite));
        }

        return 0;
    }

    public static String total(String limite){

        double totalH = 0, totalA = 0, totalC = 0;

        if (Horarios.notIsAcessado()) Horarios.abrir();
        for (Empregado e : trabalhadores){
            if (e instanceof Horista){
                totalH += bruto(e, limite);
            }
            else if (e instanceof Assalariado){
                totalA += bruto(e, limite);
            }
            else{
                totalC += bruto(e, limite);
            }
        }

        return "";
    }

    public static void rodar(){

    }
}
