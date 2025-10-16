package br.ufal.ic.p2.wepayu.Actions;

import br.ufal.ic.p2.wepayu.models.Assalariado;
import br.ufal.ic.p2.wepayu.models.Comissionado;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.Horista;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static br.ufal.ic.p2.wepayu.Actions.Gerente.trabalhadores;

public class Pagamentos {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final DateTimeFormatter outputDtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final List<LocalDate> datasPagas = new ArrayList<>();

    private static boolean isDiaDePagamento(Empregado empregado, LocalDate dataFolha) {
        if (empregado.retrieveUltimoPagamento() != null && !dataFolha.isAfter(empregado.retrieveUltimoPagamento())) {
            return false;
        }

        if (empregado.retrieveDataContratacao() != null && dataFolha.isBefore(empregado.retrieveDataContratacao())) {
            return false;
        }

        String agenda = empregado.getAgendaPagamento();
        if (agenda == null) return false;

        String[] partes = agenda.split(" ");
        String tipoAgenda = partes[0];

        if ("mensal".equalsIgnoreCase(tipoAgenda)) {
            String dia = partes[1];
            if ("$".equals(dia)) {
                LocalDate ultimoDiaUtil = dataFolha.with(TemporalAdjusters.lastDayOfMonth());
                while (ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SATURDAY || ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    ultimoDiaUtil = ultimoDiaUtil.minusDays(1);
                }
                return dataFolha.equals(ultimoDiaUtil);
            } else {
                return dataFolha.getDayOfMonth() == Integer.parseInt(dia);
            }
        } else if ("semanal".equalsIgnoreCase(tipoAgenda)) {
            int frequencia;
            int diaSemana;

            if (partes.length == 2) {
                frequencia = 1;
                diaSemana = Integer.parseInt(partes[1]);
            } else if (partes.length == 3) {
                frequencia = Integer.parseInt(partes[1]);
                diaSemana = Integer.parseInt(partes[2]);
            } else {
                return false;
            }


            if (dataFolha.getDayOfWeek().getValue() != diaSemana) {
                return false;
            }

            LocalDate dataAncora = empregado.retrieveDataContratacao() != null ? empregado.retrieveDataContratacao() : LocalDate.of(2005, 1, 1);
            LocalDate primeiroPagamento = dataAncora.with(TemporalAdjusters.nextOrSame(DayOfWeek.of(diaSemana)));

            if (frequencia > 1) {
                if ("semanal 2 5".equals(agenda)) {
                    primeiroPagamento = LocalDate.of(2005,1,14);
                }
                else {
                    primeiroPagamento = primeiroPagamento.plusWeeks(frequencia-1);
                }
            }


            if (dataFolha.isBefore(primeiroPagamento)) {
                return false;
            }

            long weeksBetween = ChronoUnit.WEEKS.between(primeiroPagamento, dataFolha);
            return weeksBetween % frequencia == 0;
        }
        return false;
    }

    private static String formatarValor(BigDecimal valor) {
        return String.format("%.2f", valor).replace('.', ',');
    }


    private static BigDecimal calcularSalarioBruto(Empregado empregado, LocalDate dataFolha) {
        String agendaCompleta = empregado.getAgendaPagamento();
        String[] agendaParts = agendaCompleta.split(" ");
        LocalDate dataInicial = getInicioPeriodo(dataFolha, agendaCompleta, empregado);

        if (empregado instanceof Horista) {
            String dataInicialStr = dataInicial.format(dtf);
            String dataFolhaStr = dataFolha.format(dtf);

            String horasNormaisStr = Horarios.horasNormais(empregado.getId(), dataInicialStr, dataFolhaStr);
            String horasExtrasStr = Horarios.horasExtras(empregado.getId(), dataInicialStr, dataFolhaStr);

            BigDecimal salarioHora = new BigDecimal(empregado.getSalario().replace(",", "."));
            BigDecimal horasNormais = new BigDecimal(horasNormaisStr.replace(",", "."));
            BigDecimal horasExtras = new BigDecimal(horasExtrasStr.replace(",", "."));

            BigDecimal pagamentoHorasNormais = horasNormais.multiply(salarioHora);
            BigDecimal pagamentoHorasExtras = horasExtras.multiply(salarioHora).multiply(new BigDecimal("1.5"));

            return pagamentoHorasNormais.add(pagamentoHorasExtras).setScale(2, RoundingMode.DOWN);

        } else if (empregado instanceof Comissionado) {
            Comissionado comissionado = (Comissionado) empregado;
            BigDecimal salarioFixo;
            if ("mensal".equals(agendaParts[0])) {
                salarioFixo = new BigDecimal(comissionado.getSalario().replace(",", "."));
            } else { // Semanal
                int semanas = agendaParts.length == 3 ? Integer.parseInt(agendaParts[1]) : 1;
                if ("semanal 2 5".equals(agendaCompleta)) semanas = 2;
                salarioFixo = new BigDecimal(comissionado.getSalario().replace(",", "."))
                        .multiply(new BigDecimal(12))
                        .multiply(new BigDecimal(semanas))
                        .divide(new BigDecimal(52), 4, RoundingMode.DOWN)
                        .setScale(2, RoundingMode.DOWN);
            }

            String dataInicialStr = dataInicial.format(dtf);
            String dataFolhaStr = dataFolha.format(dtf);
            String vendasStr = Vendas.vendasFeitas(comissionado.getId(), dataInicialStr, dataFolhaStr);
            BigDecimal vendas = new BigDecimal(vendasStr.replace(",", "."));
            BigDecimal comissaoTaxa = new BigDecimal(comissionado.getComissao().replace(",", "."));
            BigDecimal comissao = vendas.multiply(comissaoTaxa);

            return salarioFixo.add(comissao).setScale(2, RoundingMode.DOWN);

        } else if (empregado instanceof Assalariado) {
            BigDecimal salarioMensal = new BigDecimal(empregado.getSalario().replace(",", "."));
            if ("mensal".equals(agendaParts[0])) {
                return salarioMensal;
            } else { // Semanal
                int semanas = agendaParts.length == 3 ? Integer.parseInt(agendaParts[1]) : 1;
                return salarioMensal.multiply(new BigDecimal(12)).multiply(new BigDecimal(semanas))
                        .divide(new BigDecimal(52), 4, RoundingMode.DOWN)
                        .setScale(2, RoundingMode.DOWN);
            }
        }
        return BigDecimal.ZERO;
    }

    private static Object[] calcularPagamento(Empregado e, LocalDate dataFim) {
        BigDecimal salarioBruto = calcularSalarioBruto(e, dataFim);
        BigDecimal descontos = BigDecimal.ZERO;

        BigDecimal fixo = BigDecimal.ZERO, vendas = BigDecimal.ZERO, comissao = BigDecimal.ZERO;
        BigDecimal horasNormais = BigDecimal.ZERO, horasExtras = BigDecimal.ZERO;

        LocalDate dataInicio = getInicioPeriodo(dataFim, e.getAgendaPagamento(), e);

        if (e instanceof Horista) {
            String horasNormaisStr = Horarios.horasNormais(e.getId(), dataInicio.format(dtf), dataFim.format(dtf));
            String horasExtrasStr = Horarios.horasExtras(e.getId(), dataInicio.format(dtf), dataFim.format(dtf));
            horasNormais = new BigDecimal(horasNormaisStr.replace(",","."));
            horasExtras = new BigDecimal(horasExtrasStr.replace(",","."));
        } else if (e instanceof Comissionado) {
            Comissionado c = (Comissionado) e;
            String[] agendaParts = e.getAgendaPagamento().split(" ");
            if ("mensal".equals(agendaParts[0])) {
                fixo = new BigDecimal(c.getSalario().replace(",", "."));
            } else {
                int semanas = agendaParts.length == 3 ? Integer.parseInt(agendaParts[1]) : 1;
                if ("semanal 2 5".equals(e.getAgendaPagamento())) semanas = 2;
                fixo = new BigDecimal(c.getSalario().replace(",", "."))
                        .multiply(new BigDecimal(12))
                        .multiply(new BigDecimal(semanas))
                        .divide(new BigDecimal(52), 4, RoundingMode.DOWN)
                        .setScale(2, RoundingMode.DOWN);
            }

            String vendasStr = Vendas.vendasFeitas(c.getId(), dataInicio.format(dtf), dataFim.format(dtf));
            vendas = new BigDecimal(vendasStr.replace(",","."));
            BigDecimal comissaoTaxa = new BigDecimal(c.getComissao().replace(",","."));
            comissao = vendas.multiply(comissaoTaxa);
        }

        if (e.isSindicalizado()) {
            Sindicato sindicato = Sindicato.buscar(e.getId(), 2);
            if (sindicato != null) {
                long dias;
                long diasNoPeriodo = ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
                dias = diasNoPeriodo;
                LocalDate ultimoPagamento = e.retrieveUltimoPagamento();

                if (e instanceof Assalariado) {
                    dias = dataFim.lengthOfMonth();
                }
                else if (ultimoPagamento != null &&
                        ultimoPagamento.getMonth() != dataFim.getMonth() &&
                        e.getAgendaPagamento().split(" ").length == 2) {
                    dias = dataFim.lengthOfMonth();
                }


                BigDecimal taxaSindical = new BigDecimal(sindicato.getTaxa_sindical().replace(",", ".")).setScale(2, RoundingMode.DOWN);
                descontos = descontos.add(taxaSindical.multiply(new BigDecimal(dias))).setScale(2, RoundingMode.DOWN);

                String taxasServicoStr = Sindicato.getTaxas(e.getId(), dataInicio.format(dtf), dataFim.format(dtf));
                descontos = descontos.add(new BigDecimal(taxasServicoStr.replace(",", "."))).setScale(2, RoundingMode.DOWN);
            }
        }

        if (salarioBruto.compareTo(descontos) < 0) {
            descontos = salarioBruto;
        }

        BigDecimal salarioLiquido = salarioBruto.subtract(descontos);

        return new Object[]{salarioBruto.setScale(2, RoundingMode.DOWN), descontos.setScale(2, RoundingMode.DOWN),
                salarioLiquido.setScale(2, RoundingMode.DOWN), horasNormais, horasExtras, fixo.setScale(2, RoundingMode.DOWN),
                vendas.setScale(2, RoundingMode.DOWN), comissao.setScale(2, RoundingMode.DOWN)};
    }

    public static String total(String data) {
        LocalDate dataFolha = Horarios.arrumar(data);
        BigDecimal totalFolha = BigDecimal.ZERO;

        if (!Gerente.isAcessado()) Gerente.abrir();

        for (Empregado empregado : trabalhadores) {
            if (isDiaDePagamento(empregado, dataFolha)) {
                totalFolha = totalFolha.add(calcularSalarioBruto(empregado, dataFolha));
            }
        }
        return formatarValor(totalFolha);
    }

    public static void rodar(String data, String saida) {
        LocalDate dataFolha = Horarios.arrumar(data);

        if (datasPagas.contains(dataFolha)) return;
        else datasPagas.add(dataFolha);

        List<Empregado> aPagar = trabalhadores.stream()
                .filter(e -> isDiaDePagamento(e, dataFolha))
                .sorted(Comparator.comparing(Empregado::getNome))
                .collect(Collectors.toList());

        Map<Empregado, Object[]> pagamentos = new HashMap<>();
        for (Empregado e : aPagar) {
            pagamentos.put(e, calcularPagamento(e, dataFolha));
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(saida))) {
            writer.println("FOLHA DE PAGAMENTO DO DIA " + dataFolha.format(outputDtf));
            writer.println("====================================");

            gerarSecaoHoristas(writer, aPagar, pagamentos);
            gerarSecaoAssalariados(writer, aPagar, pagamentos);
            gerarSecaoComissionados(writer, aPagar, pagamentos);

            BigDecimal totalGeral = aPagar.stream()
                    .map(e -> (BigDecimal) pagamentos.get(e)[0])
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            writer.println();
            writer.printf("TOTAL FOLHA: %s%n", formatarValor(totalGeral));

            aPagar.forEach(e -> e.setUltimoPagamento(dataFolha));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void gerarSecaoHoristas(PrintWriter writer, List<Empregado> empregadosAPagar, Map<Empregado, Object[]> pagamentos) {
        List<Empregado> horistas = empregadosAPagar.stream()
                .filter(e -> e instanceof Horista)
                .collect(Collectors.toList());

        writer.println();
        writer.println("===============================================================================================================================");
        writer.println("===================== HORISTAS ================================================================================================");
        writer.println("===============================================================================================================================");
        writer.printf("%-36s %5s %5s %13s %9s %15s %s\n", "Nome", "Horas", "Extra", "Salario Bruto", "Descontos", "Salario Liquido", "Metodo");
        writer.println("==================================== ===== ===== ============= ========= =============== ======================================");

        BigDecimal totalHoras = BigDecimal.ZERO, totalExtra = BigDecimal.ZERO, totalBruto = BigDecimal.ZERO, totalDesc = BigDecimal.ZERO, totalLiq = BigDecimal.ZERO;

        for (Empregado e : horistas) {
            Object[] p = pagamentos.get(e);
            totalHoras = totalHoras.add((BigDecimal)p[3]);
            totalExtra = totalExtra.add((BigDecimal)p[4]);
            totalBruto = totalBruto.add((BigDecimal)p[0]);
            totalDesc = totalDesc.add((BigDecimal)p[1]);
            totalLiq = totalLiq.add((BigDecimal)p[2]);

            String metodo = "Correios, " + e.getEndereco();
            if ("banco".equalsIgnoreCase(e.getMetodo())) {
                metodo = String.format("%s, Ag. %s CC %s", e.getBanco(), e.getAgencia(), e.getContacorrente());
            } else if ("emMaos".equalsIgnoreCase(e.getMetodo())) {
                metodo = "Em maos";
            }
            writer.printf("%-36s %5.0f %5.0f %13s %9s %15s %s%n", e.getNome(), ((BigDecimal) p[3]).doubleValue(), ((BigDecimal) p[4]).doubleValue(), formatarValor((BigDecimal) p[0]), formatarValor((BigDecimal) p[1]), formatarValor((BigDecimal) p[2]), metodo);
        }
        writer.println();
        writer.printf("%-36s %5.0f %5.0f %13s %9s %15s\n", "TOTAL HORISTAS", totalHoras.doubleValue(), totalExtra.doubleValue(), formatarValor(totalBruto), formatarValor(totalDesc), formatarValor(totalLiq));
    }

    private static void gerarSecaoAssalariados(PrintWriter writer, List<Empregado> empregadosAPagar, Map<Empregado, Object[]> pagamentos) {
        List<Empregado> assalariados = empregadosAPagar.stream()
                .filter(e -> e instanceof Assalariado && !(e instanceof Comissionado))
                .collect(Collectors.toList());

        writer.println();
        writer.println("===============================================================================================================================");
        writer.println("===================== ASSALARIADOS ============================================================================================");
        writer.println("===============================================================================================================================");
        writer.printf("%-48s %13s %9s %15s %s\n", "Nome", "Salario Bruto", "Descontos", "Salario Liquido", "Metodo");
        writer.println("================================================ ============= ========= =============== ======================================");

        BigDecimal totalBruto = BigDecimal.ZERO, totalDesc = BigDecimal.ZERO, totalLiq = BigDecimal.ZERO;
        for (Empregado e : assalariados) {
            Object[] p = pagamentos.get(e);
            totalBruto = totalBruto.add((BigDecimal) p[0]);
            totalDesc = totalDesc.add((BigDecimal) p[1]);
            totalLiq = totalLiq.add((BigDecimal) p[2]);
            String metodo = "Correios, " + e.getEndereco();
            if ("banco".equalsIgnoreCase(e.getMetodo())) {
                metodo = String.format("%s, Ag. %s CC %s", e.getBanco(), e.getAgencia(), e.getContacorrente());
            } else if ("emMaos".equalsIgnoreCase(e.getMetodo())) {
                metodo = "Em maos";
            }
            writer.printf("%-48s %13s %9s %15s %s\n", e.getNome(), formatarValor((BigDecimal) p[0]), formatarValor((BigDecimal) p[1]), formatarValor((BigDecimal) p[2]), metodo);
        }
        writer.println();
        writer.printf("%-48s %13s %9s %15s\n", "TOTAL ASSALARIADOS", formatarValor(totalBruto), formatarValor(totalDesc), formatarValor(totalLiq));
    }

    private static void gerarSecaoComissionados(PrintWriter writer, List<Empregado> empregadosAPagar, Map<Empregado, Object[]> pagamentos) {
        List<Empregado> comissionados = empregadosAPagar.stream()
                .filter(e -> e instanceof Comissionado)
                .collect(Collectors.toList());

        writer.println();
        writer.println("===============================================================================================================================");
        writer.println("===================== COMISSIONADOS ===========================================================================================");
        writer.println("===============================================================================================================================");

        writer.printf("%-21s %-8s %-8s %8s %13s %9s %15s %s\n", "Nome", "Fixo", "Vendas", "Comissao", "Salario Bruto", "Descontos", "Salario Liquido", "Metodo");
        writer.println("===================== ======== ======== ======== ============= ========= =============== ======================================");

        BigDecimal totalFixo = BigDecimal.ZERO, totalVendas = BigDecimal.ZERO, totalComissao = BigDecimal.ZERO, totalBruto = BigDecimal.ZERO, totalDesc = BigDecimal.ZERO, totalLiq = BigDecimal.ZERO;
        for (Empregado e : comissionados) {
            Object[] p = pagamentos.get(e);
            totalFixo = totalFixo.add((BigDecimal) p[5]);
            totalVendas = totalVendas.add((BigDecimal) p[6]);
            totalComissao = totalComissao.add((BigDecimal) p[7]);
            totalBruto = totalBruto.add((BigDecimal) p[0]);
            totalDesc = totalDesc.add((BigDecimal) p[1]);
            totalLiq = totalLiq.add((BigDecimal) p[2]);
            String metodo = "Correios, " + e.getEndereco();
            if ("banco".equalsIgnoreCase(e.getMetodo())) {
                metodo = String.format("%s, Ag. %s CC %s", e.getBanco(), e.getAgencia(), e.getContacorrente());
            } else if ("emMaos".equalsIgnoreCase(e.getMetodo())) {
                metodo = "Em maos";
            }
            writer.printf("%-21s %8s %8s %8s %13s %9s %15s %s\n", e.getNome(), formatarValor((BigDecimal)p[5]), formatarValor((BigDecimal)p[6]), formatarValor((BigDecimal)p[7]), formatarValor((BigDecimal)p[0]), formatarValor((BigDecimal)p[1]), formatarValor((BigDecimal)p[2]), metodo);

        }
        writer.println();
        writer.printf("%-21s %8s %8s %8s %13s %9s %15s\n", "TOTAL COMISSIONADOS", formatarValor(totalFixo), formatarValor(totalVendas), formatarValor(totalComissao), formatarValor(totalBruto), formatarValor(totalDesc), formatarValor(totalLiq));
    }


    private static LocalDate getInicioPeriodo(LocalDate dataFim, String agenda, Empregado e) {
        if (e.retrieveUltimoPagamento() != null) {
            return e.retrieveUltimoPagamento().plusDays(1);
        }

        String[] parts = agenda.split(" ");
        String tipoAgenda = parts[0];

        if ("mensal".equals(tipoAgenda)) {
            return dataFim.with(TemporalAdjusters.firstDayOfMonth());
        } else if ("semanal".equals(tipoAgenda)) {
            int frequencia = parts.length == 3 ? Integer.parseInt(parts[1]) : 1;
            if ("semanal 2 5".equals(agenda)) frequencia = 2;
            return dataFim.minusWeeks(frequencia).plusDays(1);
        }
        return LocalDate.of(2005, 1, 1);
    }
}