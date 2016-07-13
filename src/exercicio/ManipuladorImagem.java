//Aluna: Barbara Gonçalves
package exercicio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManipuladorImagem {

	private int tipoArquivo;
	public int largura = 0;
	public int altura = 0;
	public Double[][] matriz;
	public int tomMaximo = -1;
	public String cabecalho = "";

	private Double escalaMin;
	private Double escalaMax;
	private File f;

	public int bordaUp = 0;
	public int bordaDown = 0;
	public int bordaLeft = 0;
	public int bordaRight = 0;

	public Double[][] matrizCompara;
	public int larguraC = 0;
	public int alturaC = 0;
	public int tomMaximoC = -1;
	public String cabecalhoC = "";

	public int tp = 0;
	public int fp = 0;
	public int tn = 0;
	public int fn = 0;
	public int positivo = 0;
	public int negativo = 0;

	public static final int px_ativo = 1;
	public static final int px_inativo = 0;

	public double PRETO = 0.0;
	public double BRANCO = 255.0;

	public void lerImagem(String path) throws IOException {
		this.f = new File(path);
		BufferedReader leitor = new BufferedReader(new FileReader(this.f));

		String linhaArquivo;
		int i = 0;
		int j = 0;
		boolean achouTomMaximo = false;
		boolean num = false;
		linhaArquivo = leitor.readLine();

		while (linhaArquivo != null) {

			if (linhaArquivo != null && linhaArquivo.equals("P2")) {
				cabecalho += linhaArquivo + "\n";
			}
			linhaArquivo = leitor.readLine();

			if (linhaArquivo != null && !achouTomMaximo) {
				String[] vetorTemp = linhaArquivo.split(" ");
				if (ehTomMaximo(vetorTemp)) {
					achouTomMaximo = true;
					cabecalho += "\n";
				}
				cabecalho += linhaArquivo + "\n";
			}

			if (linhaArquivo != null && !linhaArquivo.contains("#")
					&& !linhaArquivo.isEmpty()) {

				String[] vetorTemp = linhaArquivo.split(" ");

				if (vetorTemp != null && vetorTemp.length == 2
						&& this.largura == 0 && this.altura == 0) {
					this.largura = Integer.parseInt(vetorTemp[0]);
					this.altura = Integer.parseInt(vetorTemp[1]);

					this.matriz = new Double[altura][largura];

				} else {
					for (int index = 0; index < vetorTemp.length; index++) {
						Double.parseDouble(vetorTemp[index]);
						num = true;
					}

					if (num && !ehTomMaximo(vetorTemp) && largura > 0
							&& altura > 0) {
						for (int index = 0; index < vetorTemp.length; index++) {
							if (j == largura) {
								j = 0;
								i++;
							}
							if (i == altura)
								break;
							this.matriz[i][j] = Double
									.parseDouble(vetorTemp[index]);
							j++;
						}
					}
				}
			}
		}
		leitor.close();
	}

	/**
	 * Salvar Imagem
	 * 
	 * @throws IOException
	 */
	public void salvar() throws IOException {
		FileWriter fw = new FileWriter(this.f);
		BufferedWriter bw = new BufferedWriter(fw);

		StringBuilder content = new StringBuilder();
		content.append(cabecalho);
		for (int i = 0; i < altura; i++) {
			for (int j = 0; j < largura; j++) {
				content.append(matriz[i][j].intValue());
				content.append(" ");
			}
		}

		bw.write(content.toString());
		bw.close();
	}

	// Resolução da questao 1

	/**
	 * Método para alterar o contraste da imagem
	 * 
	 * @param contraste
	 */

	public void alterarContraste(double contraste) {

		for (int i = 0; i < this.altura; i++) {
			for (int j = 0; j < this.largura; j++) {

				this.matriz[i][j] *= contraste;

				if (matriz[i][j] < 0) {
					matriz[i][j] = 0.0;
				}
				if (matriz[i][j] > 255) {
					matriz[i][j] = 255.0;
				}
			}
		}
	}

	/**
	 * Método para alterar o brilho da imagem
	 * 
	 * @param brilho
	 */
	public void alterarBrilho(int brilho) {

		for (int i = 0; i < this.altura; i++) {
			for (int j = 0; j < this.largura; j++) {

				this.matriz[i][j] += brilho;

				if (matriz[i][j] < 0) {
					matriz[i][j] = 0.0;
				}
				if (matriz[i][j] > 255) {
					matriz[i][j] = 255.0;
				}
			}
		}
	}

	/**
	 * Método para negativar a imagem
	 */
	public void negativarImagem() {
		double modulo;
		for (int i = 0; i < this.altura; i++) {
			for (int j = 0; j < this.largura; j++) {
				modulo = this.tomMaximo - this.matriz[i][j];
				this.matriz[i][j] = Math.abs(modulo);
			}
		}
	}

	/**
	 * Calcula quantas vezes o pixel h aparece na matriz;
	 * 
	 * @return vetor h com a quantidade de repetições de cada pixel (de 0 a 255)
	 */
	public int[] calculoH() {
		// quantidade de vezes que o pixel aparece
		int h[] = new int[256];

		for (int i = 0; i < this.altura; i++) {
			for (int j = 0; j < this.largura; j++) {
				int cont = h[this.matriz[i][j].intValue()];
				h[this.matriz[i][j].intValue()] = ++cont;
			}
		}
		return h;
	}

	/**
	 * Método para equalizar a imagem
	 */
	public void equilizar() {
		int h[] = new int[256];
		double hist[] = new double[256];
		double g[] = new double[256];
		int f[] = new int[256];

		h = this.calculoH();
		hist = this.calculoHist(h);
		g = this.calculoG(hist);
		f = this.calculoFk(g);

		for (int i = 0; i < this.altura; i++) {
			for (int j = 0; j < this.largura; j++) {
				this.matriz[i][j] = (double) f[this.matriz[i][j].intValue()];
			}
		}
	}

	/*
	 * Aplicando a fórmula hist(k)= h(k)/n
	 */
	public double[] calculoHist(int[] h) {
		double hist[] = new double[256];
		for (int i = 0; i < h.length; i++) {
			hist[i] = ((double) h[i]) / (this.altura * this.largura);
		}
		return hist;
	}

	/**
	 * Método para calcular g da fórmula g(k)= hist(i) (probabilidade acumulada
	 * / somatório)
	 * 
	 * @param hist
	 * @return
	 */
	public double[] calculoG(double[] hist) {
		double g[] = new double[256];

		for (int k = 0; k < g.length; k++) {
			for (int i = k; i >= 0; i--) {
				g[k] += hist[i];
			}
		}
		return g;
	}

	public int[] calculoFk(double[] g) {
		int f[] = new int[256];
		for (int i = 0; i < g.length; i++) {
			f[i] = Integer.parseInt(String.valueOf(Math.round(g[i]
					* this.tomMaximo)));
		}
		return f;
	}

	public double valorMax() {
		double max = 0;
		// buscando o valor maximo
		for (int i = 0; i < this.altura; i++) {
			for (int j = 0; j < this.largura; j++) {
				if (this.matriz[i][j] > max) {
					max = this.matriz[i][j];
				}
			}
		}
		return max;
	}

	public double valorMin() {
		double min = 255.0;
		// buscando o valor mínimo
		for (int i = 0; i < this.altura; i++) {
			for (int j = 0; j < this.largura; j++) {
				if (this.matriz[i][j] < min) {
					min = this.matriz[i][j];
				}
			}
		}
		return min;
	}

	/**
	 * Método para fazer a mudança de escala da imagem (expansão)
	 * 
	 * @param gMin
	 * @param gMax
	 */
	public void mudarEscala(double gMin, double gMax) {
		double fMax;
		double fMin;

		fMax = this.valorMax();
		fMin = this.valorMin();

		for (int i = 0; i < this.altura; i++) {
			for (int j = 0; j < this.largura; j++) {
				this.matriz[i][j] = ((gMax - gMin) / (fMax - fMin))
						* (matriz[i][j] - fMin) + gMin;
			}
		}

	}

	// Resolução da questao 2 - Filtros

	// Método para achar o pixel central do filtro
	public void verificaPixelCentral(int[][] matrizFiltro) {
		int pixelCentralLinha = 0;
		int pixelCentralColuna = 0;

		pixelCentralLinha = matrizFiltro.length / 2;
		pixelCentralColuna = matrizFiltro[0].length / 2;

		this.bordaUp = pixelCentralLinha;
		this.bordaDown = matrizFiltro.length - pixelCentralLinha - 1;
		this.bordaLeft = pixelCentralColuna;
		this.bordaRight = matrizFiltro[pixelCentralLinha].length
				- pixelCentralColuna - 1;
	}

	// Método para gerar matriz nova com as bordas replicadas
	public double[][] matrizComBordas(Double[][] matrizInicial) {

		int linhaBordas = this.matriz.length + this.bordaUp + this.bordaDown;
		int colunaBordas = this.matriz[0].length + this.bordaLeft
				+ this.bordaRight;

		double novaMatrizBordas[][] = new double[linhaBordas][colunaBordas];

		for (int i = this.bordaUp; i < novaMatrizBordas.length - this.bordaDown; i++) {
			for (int j = this.bordaLeft; j < novaMatrizBordas[0].length
					- this.bordaRight; j++) {
				try {
					novaMatrizBordas[i][j] = matrizInicial[i - this.bordaUp][j
							- this.bordaLeft];
				} catch (Exception e) {
					// System.out.println("Pam!");
				}
			}
		}

		return novaMatrizBordas;
	}

	/**
	 * Método que passa o filtro pela imagem
	 * 
	 * @param matrizFiltro
	 * @param fatorMultiplicador
	 */
	public void filtragem(int[][] matrizFiltro, int fatorMultiplicador) {

		this.verificaPixelCentral(matrizFiltro);
		double[][] matrizModificada = this.matrizComBordas(this.matriz);
		double[][] matrizRetorno = new double[matrizModificada.length][matrizModificada[0].length];

		int aux = 0;
		// loop para o pixel central percorrer a imagem
		for (int linhaMatriz = this.bordaUp; linhaMatriz < matrizModificada.length
				- this.bordaDown; linhaMatriz++) {
			for (int colunaMatriz = this.bordaLeft; colunaMatriz < matrizModificada[linhaMatriz].length
					- this.bordaRight; colunaMatriz++) {
				aux = 0;
				// loop para percorrer o filtro e realizar o cálculo;
				for (int linhaFiltro = 0; linhaFiltro < matrizFiltro.length; linhaFiltro++) {
					for (int colunaFiltro = 0; colunaFiltro < matrizFiltro[linhaFiltro].length; colunaFiltro++) {
						aux += matrizFiltro[linhaFiltro][colunaFiltro]
								* matrizModificada[linhaMatriz + linhaFiltro
										- this.bordaUp][colunaMatriz
										+ colunaFiltro - this.bordaUp];
					}
				}
				if (fatorMultiplicador > 0) {
					aux = Math.round(aux / fatorMultiplicador);
					if (aux > 255) {
						aux = 255;
					}
					if (aux < 0) {
						aux = 0;
					}
				}
				matrizRetorno[linhaMatriz][colunaMatriz] = this.truncarTons(
						aux, 255, 0);
			}
		}

		this.setMatriz(this.matrizFiltrada(matrizRetorno));
	}

	// Método que Retorna matriz filtrada sem as bordas
	public Double[][] matrizFiltrada(double[][] matrizComFiltro) {

		int linhasMatriz = matrizComFiltro.length - this.bordaUp
				- this.bordaDown;
		int colunasMatriz = matrizComFiltro[0].length - this.bordaLeft
				- this.bordaRight;

		Double[][] matrizRetorno = new Double[linhasMatriz][colunasMatriz];

		for (int i = this.bordaUp; i < matrizComFiltro.length - this.bordaDown; i++) {
			for (int j = this.bordaLeft; j < matrizComFiltro[0].length
					- this.bordaRight; j++) {
				matrizRetorno[i - this.bordaUp][j - this.bordaLeft] = matrizComFiltro[i][j];
			}
		}

		return matrizRetorno;
	}

	// Questão 3 - Tratamento de ruídos

	/**
	 * Método para tratar ruídos utilizando o cálculo da mediana Armazena numa
	 * lista os valores dentro de uma matriz "filtro" e é calculada a mediana;
	 * 
	 * @param tamanhoMatriz
	 */
	public void mediana(int tamanhoMatriz) {
		int[][] matrizFiltro = new int[tamanhoMatriz][tamanhoMatriz];
		this.verificaPixelCentral(matrizFiltro);
		List<Double> mediana;
		double[][] matrizOriginal = this.matrizComBordas(this.matriz);
		for (int linhaMatriz = this.bordaUp; linhaMatriz < matrizOriginal.length
				- this.bordaDown; linhaMatriz++) {
			for (int colImagem = this.bordaLeft; colImagem < matrizOriginal[linhaMatriz].length
					- this.bordaRight; colImagem++) {
				mediana = new ArrayList<Double>();
				for (int linhaFiltro = 0; linhaFiltro < matrizFiltro.length; linhaFiltro++) {
					for (int colunaFitro = 0; colunaFitro < tamanhoMatriz; colunaFitro++) {
						try {

							mediana.add(matrizOriginal[linhaMatriz
									+ linhaFiltro - this.bordaUp][colImagem
									+ colunaFitro - this.bordaLeft]);

						} catch (Exception e) {
							System.out.println(" ");
						}
					}
				}
				Collections.sort(mediana);
				matrizOriginal[linhaMatriz][colImagem] = mediana.get(mediana
						.size() / 2);
			}
		}
		this.setMatriz(this.matrizFiltrada(matrizOriginal));
	}

	// Binarização / Questão 4

	public int calcularLimiar() {
		int limiar = 127;
		int novoLimiar = 0;
		int limiarInicial = limiar;
		int somaDimensoes = 0;
		// calcular h - segmentar
		int[] hG1 = new int[256];
		int[] hG2 = new int[256];
		somaDimensoes = (this.largura) * (this.altura);
		int cont = 0;
		int cont2 = 0;

		do {

			for (int i = 0; i < this.altura; i++) {
				for (int j = 0; j < this.largura; j++) {
					if (this.matriz[i][j] <= limiar) {
						cont = hG1[this.matriz[i][j].intValue()];
						hG1[this.matriz[i][j].intValue()] = ++cont;
					}
					if (this.matriz[i][j] > limiar) {
						cont2 = hG2[this.matriz[i][j].intValue()];
						hG2[this.matriz[i][j].intValue()] = ++cont2;
					}
				}
			}

			// calcular nivel de cinza medio de G1
			int somaG1 = 0;

			for (int i = 0; i < hG1.length; i++) {
				for (int j = 0; j < hG1.length; j++) {
					somaG1 += hG1[i];
				}
			}
			int mediaG1 = 0;

			mediaG1 = somaG1 / somaDimensoes;

			// calcular nivel de cinza medio de G2
			int somaG2 = 0;

			for (int i = 0; i < hG2.length; i++) {
				for (int j = 0; j < hG2.length; j++) {
					somaG2 += hG2[i];
				}
			}
			int mediaG2 = 0;

			mediaG2 = somaG2 / somaDimensoes;

			novoLimiar = (mediaG1 + mediaG2) / 2;
			limiar = novoLimiar;
		} while (novoLimiar > limiarInicial);

		return limiar;

	}

	public void binariza() {
		double limiar = 0;
		limiar = this.calcularLimiar();
		for (int i = 0; i < this.altura; i++) {
			for (int j = 0; j < this.largura; j++) {
				if (matriz[i][j] < limiar) {
					matriz[i][j] = 0.0;

				}
				if (matriz[i][j] > limiar) {
					matriz[i][j] = 255.0;
				}
			}
		}
	}

	public void comparaPositivo() {
		int positivo = 0;
		int negativo = 0;
		int tp = 0;
		int fn = 0;
		int fp = 0;
		int tn = 0;
		for (int i = 0; i < this.matriz.length; i++) {
			for (int j = 0; j < this.matriz[0].length; j++) {
				if (this.matrizCompara[i][j] == 255 && this.matriz[i][j] == 255) {
					tp++;
				}
				if (this.matriz[i][j] == 0 && this.matrizCompara[i][j] == 255) {
					fn++;
				}
				if (this.matriz[i][j] == 0 && this.matriz[i][j] == 0) {
					tn++;
				}
				if (this.matriz[i][j] == 255 && this.matrizCompara[i][j] == 0) {
					fp++;
				}
			}
		}
		this.setTn(tn);
		this.setTp(tp);
		this.setFn(fn);
		this.setFp(fp);

		positivo = tp + fn;
		negativo = fp + tn;

		this.setPositivo(positivo);
		this.setNegativo(negativo);
		// return positivo;
	}

	public float precisao() {
		float precisao = 0;

		this.comparaPositivo();
		float tp = this.getTp();
		float fp = this.getFp();

		precisao = tp / (tp + fp);
		System.out.println(precisao);
		return precisao;
	}

	public float recall() {
		float recall = 0;
		this.comparaPositivo();
		float tp = this.getTp();
		float pos = this.getPositivo();
		recall = tp / pos;
		System.out.println(recall);
		return recall;
	}

	public float accuracy() {
		float accuracy = 0;

		this.comparaPositivo();
		float tp = this.getTp();
		float tn = this.getTn();
		float p = this.getPositivo();
		float n = this.getNegativo();

		accuracy = (tp + tn) / (p + n);
		System.out.println(accuracy);
		return accuracy;
	}

	public float especificidade() {
		float especificidade = 0;
		this.comparaPositivo();

		float tn = this.getTn();
		float n = this.getNegativo();

		especificidade = tn / n;
		System.out.println(especificidade);
		return especificidade;
	}

	public float fMeasure() {
		float porcentagem = 0;

		porcentagem = (2 * (this.recall() * this.precisao()) * 100)
				/ this.recall() + this.precisao();
		System.out.println(porcentagem);
		return porcentagem;
	}

	// Questão 5

	// Questão 6 - Dilatação
	// Questão 7 - Erosão

	public void lerImagemIdeal(String path) throws IOException {
		this.f = new File(path);
		BufferedReader leitor = new BufferedReader(new FileReader(this.f));

		String linhaArquivo;
		int i = 0;
		int j = 0;
		boolean achouTomMaximo = false;
		boolean num = false;
		linhaArquivo = leitor.readLine();

		while (linhaArquivo != null) {

			if (linhaArquivo != null && linhaArquivo.equals("P2")) {
				cabecalhoC += linhaArquivo + "\n";
			}
			linhaArquivo = leitor.readLine();

			if (linhaArquivo != null && !achouTomMaximo) {
				String[] vetorTemp = linhaArquivo.split(" ");
				if (ehTomMaximo(vetorTemp)) {
					achouTomMaximo = true;
					cabecalhoC += "\n";
				}
				cabecalhoC += linhaArquivo + "\n";
			}

			if (linhaArquivo != null && !linhaArquivo.contains("#")
					&& !linhaArquivo.isEmpty()) {

				String[] vetorTemp = linhaArquivo.split(" ");

				if (vetorTemp != null && vetorTemp.length == 2
						&& this.larguraC == 0 && this.alturaC == 0) {
					this.larguraC = Integer.parseInt(vetorTemp[0]);
					this.alturaC = Integer.parseInt(vetorTemp[1]);

					this.matrizCompara = new Double[alturaC][larguraC];

				} else {
					for (int index = 0; index < vetorTemp.length; index++) {
						Double.parseDouble(vetorTemp[index]);
						num = true;
					}

					if (num && !ehTomMaximo(vetorTemp) && larguraC > 0
							&& alturaC > 0) {
						for (int index = 0; index < vetorTemp.length; index++) {
							if (j == larguraC) {
								j = 0;
								i++;
							}
							if (i == alturaC)
								break;
							this.matrizCompara[i][j] = Double
									.parseDouble(vetorTemp[index]);
							j++;
						}
					}
				}
			}
		}
		leitor.close();
	}

	/*
	 * Implementar o algoritmo de slide.Parâmetros de entrada: imagem, limiar e
	 * matriz de difusão do erro.
	 */

	public void difusao() {
		int limiar = 0;
		limiar = this.calcularLimiar();
		double erro = 0.0;
		double oldPx;

		for (int i = 0; i < this.largura; i++)
			for (int j = 0; j < this.altura; j++) {

				oldPx = this.matriz[i][j];
				if (oldPx < limiar) {
					this.matriz[i][j] = 0.0;
					erro = oldPx - 0;
				} else {
					this.matriz[i][j] = 255.0;
					erro = oldPx - 255;

				}
				try {
					this.matriz[i + 1][j] += ((3 * erro) / 8);
					this.matriz[i][j + 1] += ((3 * erro) / 8);
					this.matriz[i + 1][j + 1] += ((2 * erro) / 8);
					// this.matriz[i+1][j+1] += (erro/16);

				} catch (ArrayIndexOutOfBoundsException e) {
					continue;
					// tratar exception!
				}
			}
	}

	public int getTipoArquivo() {
		return tipoArquivo;
	}

	public void setTipoArquivo(int tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}

	public int getLargura() {
		return largura;
	}

	public void setLargura(int largura) {
		this.largura = largura;
	}

	public int getAltura() {
		return altura;
	}

	public void setAltura(int altura) {
		this.altura = altura;
	}

	public Double[][] getMatriz() {
		return matriz;
	}

	public void setMatriz(Double[][] matriz) {
		this.matriz = matriz;
	}

	public int getTomMaximo() {
		return tomMaximo;
	}

	public void setTomMaximo(int tomMaximo) {
		this.tomMaximo = tomMaximo;
	}

	public Double getEscalaMin() {
		return escalaMin;
	}

	public void setEscalaMin(Double escalaMin) {
		this.escalaMin = escalaMin;
	}

	public Double getEscalaMax() {
		return escalaMax;
	}

	public void setEscalaMax(Double escalaMax) {
		this.escalaMax = escalaMax;
	}

	public String getCabecalho() {
		return cabecalho;
	}

	public void setCabecalho(String cabecalho) {
		this.cabecalho = cabecalho;
	}

	public Double[][] getMatrizCompara() {
		return matrizCompara;
	}

	public void setMatrizCompara(Double[][] matrizCompara) {
		this.matrizCompara = matrizCompara;
	}

	public int getLarguraC() {
		return larguraC;
	}

	public void setLarguraC(int larguraC) {
		this.larguraC = larguraC;
	}

	public int getAlturaC() {
		return alturaC;
	}

	public void setAlturaC(int alturaC) {
		this.alturaC = alturaC;
	}

	public int getTp() {
		return tp;
	}

	public void setTp(int tp) {
		this.tp = tp;
	}

	public int getFp() {
		return fp;
	}

	public void setFp(int fp) {
		this.fp = fp;
	}

	public int getTn() {
		return tn;
	}

	public void setTn(int tn) {
		this.tn = tn;
	}

	public int getFn() {
		return fn;
	}

	public void setFn(int fn) {
		this.fn = fn;
	}

	public int getPositivo() {
		return positivo;
	}

	public void setPositivo(int positivo) {
		this.positivo = positivo;
	}

	public int getNegativo() {
		return negativo;
	}

	public void setNegativo(int negativo) {
		this.negativo = negativo;
	}

	/**
	 * Busca o tom máximo da imagem
	 * 
	 * @param vetorTemp
	 * @return
	 */
	private boolean ehTomMaximo(String[] vetorTemp) {
		if (vetorTemp != null && vetorTemp.length == 1) {
			try {
				tomMaximo = Integer.parseInt(vetorTemp[0]);
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		return false;
	}

	/**
	 * Método para truncar os valores dos tons;
	 * 
	 * @param tons
	 * @param valorMax
	 * @param valorMin
	 * @return
	 */
	private int truncarTons(int tons, int valorMax, int valorMin) {
		if (tons > valorMax) {
			tons = valorMax;
		}
		if (tons < valorMin) {
			tons = valorMin;
		}
		return tons;
	}

	/**
	 * Salvar Imagem
	 * 
	 * @throws IOException
	 */
	public void salvarIdeal() throws IOException {
		FileWriter fw = new FileWriter(this.f);
		BufferedWriter bw = new BufferedWriter(fw);

		StringBuilder content = new StringBuilder();
		content.append(cabecalhoC);
		for (int i = 0; i < this.alturaC; i++) {
			for (int j = 0; j < this.larguraC; j++) {
				content.append(matrizCompara[i][j].intValue());
				content.append(" ");
			}
		}

		bw.write(content.toString());
		bw.close();
	}

	/**
	 * Método base para copiar para outra pasta a imagem original. Será
	 * utilizado como base para aplicar as questões do exercício. -BKP
	 * 
	 * @throws IOException
	 */
	public void copy() throws IOException {
		File src = new File(
				"/Users/Babi/workspace/ExercícioAspectos/src/exercicio/lena.pgm");
		File dest = new File(
				"/Users/Babi/workspace/ExercícioAspectos/src/img/lenaCopia.pgm");
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dest); // Transferindo bytes de
														// entrada para saída
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	/**
	 * Método para ler a imagem passando o caminho onde a imagem está
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void lerImagemTeste(String path) throws IOException {
		File file = new File(path);
		String linhaArquivo = null;
		BufferedReader leitor = new BufferedReader(new FileReader(file));
		linhaArquivo = leitor.readLine();
		while (linhaArquivo != null) {
			linhaArquivo = leitor.readLine();

			if (linhaArquivo != null) {
				System.out.println(linhaArquivo);
			}
		}

		leitor.close();
	}

}
