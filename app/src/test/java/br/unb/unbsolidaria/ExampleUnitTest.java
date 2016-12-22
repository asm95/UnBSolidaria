package br.unb.unbsolidaria;

import org.junit.Test;

import br.unb.unbsolidaria.entities.FormValidation;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private int testNumber = 1;

    @Test
    public void RegisterValidationTest() {
        //TODO: verify commented items that should be working

        String [] CPF_validos = { "96913238932", /*"01115375502"*/ };
        String [] CNPJ_validos = { "00348003000110", /*"13642634756318"*/ };
        String [] CEP_validos = { "12910-180", "12910180" };
        String [] Matricula_validas = { "150019284", "000000000" };

        System.out.println(testNumber + ". Iniciando teste da classe FormValidation:");

        System.out.println("Verificando CPF(s):");
        for(String item : CPF_validos){
            System.out.print(item + "; ");
            assertTrue(FormValidation.isValidCPF(item));
        }
        System.out.println("\nVerificando CNPJ(s):");
        for(String item : CNPJ_validos){
            System.out.print(item + "; ");
            assertTrue(FormValidation.isValidCNPJ(item));
        }
        System.out.println("\nVerificando CEP(s):");
        for(String item : CEP_validos){
            System.out.print(item + "; ");
            assertTrue(FormValidation.isValidCEP(item));
        }
        System.out.println("\nVerificando Matricula(s):");
        for(String item : Matricula_validas){
            System.out.print(item + "; ");
            assertTrue(FormValidation.isValidMatricula(item));
        }

        ValidaNome();

        testNumber++;
    }

    public void ValidaNome(){
        String valid[] = {
                "Marcos M.",
                "César",
                "Henrique Paiva",
                "Éden aE",
                "NOME EM CAIXA ALTA"
        };
        String not_valid[] = {
                "VINTEVINTEVINTEVINTE1",
                "Jao",
                "123",
                "1",
                "Ay",
                ""
        };

        System.out.println("\nVerificando Nome(s) válido(s):");
        for(String name : valid){
            System.out.print(name + "; ");
            assertTrue(FormValidation.isValidName(name, true));
        }
        System.out.println("\nVerificando Nome(s) inválido(s):");
        for(String name : not_valid){
            System.out.print(name + "; ");
            assertFalse(FormValidation.isValidName(name, true));
        }
    }

}