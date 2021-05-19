package it.polito.tdp.borders.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

public class Simulatore {
	
	//Modello -> qual e' lo stato del sistema ad ogni passo
	private Graph<Country,DefaultEdge> grafo;
	
	//Tipi di evento -> coda prioritaria
	private PriorityQueue<Evento> queue;
	
	//Parametri della simulazione
	private int N_MIGRANTI=1000;
	private Country partenza;//quello che l'utente seleziona
	
	//Valori in output
	private int T=-1; //passi simulati
	private Map<Country,Integer> stanziali; //varia durante la simulazione, modo rapido per cambiare valore data la chiave: con la mappa!
	
	public void init(Country country, Graph<Country,DefaultEdge> grafo) {
		this.partenza=country;
		this.grafo=grafo;
		
		//imposto lo stato iniziale
		this.T=1;
		this.stanziali=new HashMap<Country,Integer>();
		
		for(Country c:this.grafo.vertexSet()) {
			stanziali.put(c, 0); //imposto tutti gli stati a 0, non hanno ancora nessun migrante
		}
		
		//creo la coda, si riempie di eventi durante la simulazione
		this.queue=new PriorityQueue<Evento>();
		//inserisco primo evento
		this.queue.add(new Evento(T, partenza, N_MIGRANTI));
	}
	
	public void run() {
		//Finche' la coda non si svota: prendo un evento per volta e lo eseguo
		//all'inizio nella coda ho un evento; quello alla riga 43
		Evento e;
		while((e=this.queue.poll())!=null) {
			//simulo evento e
			
			this.T=e.getT();
			int nPersone=e.getN();
			Country stato=e.getCountry();
			
			//ottengo i vicini di 'stato'
			List<Country> vicini=Graphs.neighborListOf(this.grafo, stato);
		
			//nPersone/2 si spostano, si dividono in parti uguali negli stati vicini
			//nPersone/2/vicini.size();
			//nPersone=10
			//persone che si spostano=5 e 5 stanziali--> diventano tutte stanziali per via della divisione
			//se i vicini(vicini.size()) sono 7
			//migranti per stato=0; -->5/7=0 (ho troncamento per difetto)
			int migrantiPerStato=(nPersone/2)/vicini.size();//num di migranti che si devono spostare in ogni stato vicino
			
			if(migrantiPerStato>0){//c'e' almeno una persona che finisce in un altro stato, altrimenti diventano tutte stanziali
				for(Country confinante: vicini) {
					queue.add(new Evento(e.getT()+1,confinante,migrantiPerStato));
				}
			}
			
			int stanziali=nPersone-migrantiPerStato*vicini.size();//stanziali puo essere maggiore del 50 percento a causa della else
			
			this.stanziali.put(stato, this.stanziali.get(stato)+stanziali);//le persone si muovono e possono ritornare piu volte nello stesso stato
		
		}
	}
	
	public Map<Country,Integer> getStanziali(){
		return this.stanziali;
	}
	
	public Integer getT() {
		return T;
	}
}
