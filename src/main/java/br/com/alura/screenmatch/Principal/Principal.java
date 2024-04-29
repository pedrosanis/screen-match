package br.com.alura.screenmatch.Principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.*;

public class Principal {
    Scanner scanner = new Scanner(in);
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    public void exibeMenu() {
        out.println("Digite o nome da série para busca");
        var nomeSerie = scanner.nextLine();
        var json = consumoApi.obterDados(ENDERECO
                + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumoApi.obterDados(ENDERECO
                    + nomeSerie.replace(" ", "+")
                    + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);

        }
//        temporadas.forEach(System.out::println);

        for (int i = 0; i < dados.totalTemporadas(); i++) {
            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
            for (int j = 0; j < episodiosTemporada.size(); j++) {
//                out.println(episodiosTemporada.get(j).titulo());
            }
        }

//        temporadas.forEach(t -> t.episodios().forEach(e -> out.println(e.titulo())));

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());

//        episodios.forEach(out::println);

//        out.println("Digite o nome do episodio");
//        var trechoTitulo = scanner.nextLine();
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
//                .findFirst();
//        if(episodioBuscado.isPresent())
//        {
//            out.println("Episodio encontrado"+"\nTemporada: "
//                    + episodioBuscado.get().getTemporada()
//                    + "\nEpisodio: " +episodioBuscado.get().getNumeroEpisodio()+"\n"
//                    +episodioBuscado.get().getTitulo());
//        } else {
//            out.println("Episodio nao encontrado");
//        }

//            out.println("\nTop 10 Eps");
//            dadosEpisodios.stream()
//                    .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                    .peek(e -> out.println("Primeiro Filtro (N/A) " + e))
//                    .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                    .peek(e -> out.println("Ordenação " + e))
//                    .limit(10)
//                    .map(e -> e.titulo().toUpperCase())
//                    .peek(e -> System.out.println("Mapeamento " + e))
//                    .forEach(out::println);
//
//        System.out.println("Ano minimo");
//        var ano = scanner.nextInt();
//        scanner.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano,1, 1);
//
//        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodios.stream()
//                .filter(episodio -> episodio.getDataLancamento()!= null
//                        && episodio.getDataLancamento().isAfter(dataBusca))
//                .sorted(Comparator.comparing(Episodio::getDataLancamento))
//                .forEach(episodio -> out.println(
//                        "Temporada: " + episodio.getTemporada() +
//                                "Episodio: " + episodio.getTitulo() +
//                                "Data lançamento: " + episodio.getDataLancamento().format(f)
//                ));

//        List<String> instrutores = Arrays.asList("Jaqueline", "Iasmin", "Paulo");
//        instrutores.stream()
//                .sorted()
//                .limit(2)
//                .filter(n -> n.startsWith("I"))
//                .map(n -> n.toUpperCase())
//                .forEach(System.out::println);

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0d)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));
//        out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0d)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        out.println("Quantidade: " + est.getCount()+" espisodios"+
                "\nMédia: " + est.getAverage() +
                "\nMelhor Ep: " + est.getMax()+
                "\nPior Ep: " + est.getMin());
    }
}
