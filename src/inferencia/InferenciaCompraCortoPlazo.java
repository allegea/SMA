/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package inferencia;

import nrc.fuzzy.*;
//import java.io.*;

/**
 *
 * @author Sebas
 */
public class InferenciaCompraCortoPlazo {

    public double probabilidadEscogencia(double rent, double vol, double prop)
    {
        // Variable que contiene el valor de salida la cual es la probabilidad
        // de escoger una accion
        double probabilidad = 0;
        // Coordenadas del termino bajo de la variable rentabilidad
        double xRentBajo[] = {-0.1, 0.1};
        double yRentBajo[] = {1, 0};
        // Coordenadas del termino alto de la variable rentabilidad
        double xRentAlto[] = {0.1, 0.5};
        double yRentAlto[] = {0, 1};
        // Coordenadas del termino bajo de la variable volatilidad
        double xVolBajo[] = {0, 0.05};
        double yVolBajo[] = {1, 0};
        // Coordenadas del termino alto de la variable volatilidad
        double xVolAlto[] = {0.1, 0.3};
        double yVolAlto[] = {0, 1};
        // Coordenadas del termino poco de la variable propension
        double xPropPoco[] = {0, 0.5};
        double yPropPoco[] = {1, 0};
        // Coordenadas del termino muy de la variable propension
        double xPropMuy[] = {0.5, 1};
        double yPropMuy[] = {0, 1};
        // Coordenadas del termino muy bajo de la variable probabilidad
        double xProbMuyBajo[] = {0, 0.3};
        double yProbMuyBajo[] = {1, 0};
        // Coordenadas del termino muy alto de la variable probabilidad
        double xProbMuyAlto[] = {0.7, 1};
        double yProbMuyAlto[] = {0, 1};
        // Vector que contiene los resultados de ejecutar las reglas
        FuzzyValueVector fvv;
        // Variable que contiene el resultado de unir los resultados parciales
        // de todas las reglas
        FuzzyValue salidaGlobal = null;

        // Los valores de entrada no se pueden salir del Universo de Discurso
        if (rent < -0.1) rent = -0.1;
        if (rent > 0.5) rent = 0.5;
        if (vol < 0) vol = 0;
        if (vol > 0.3) vol = 0.3;
        if (prop < 0) prop = 0;
        if (prop > 1) prop = 1;

        try {
            // Variable difusa rentabilidad que contiene los terminos: bajo, medio, alto
            FuzzyVariable rentabilidad = new FuzzyVariable("rentabilidad", -0.1, 0.5, "");
            // Conjunto difuso: Linea recta con coordenadas: (-0.1, 1), (0.1, 0)
            rentabilidad.addTerm("bajo", xRentBajo, yRentBajo, 2);
            // Conjunto difuso: Triangulo con coordenadas: (-0.1, 0), (0.1, 1), (0.3, 0)
            rentabilidad.addTerm("medio", new TriangleFuzzySet(-0.1, 0.1, 0.3));
            // Conjunto difuso: Linea recta con coordenadas: (0.1, 0), (0.5, 1)
            rentabilidad.addTerm("alto", xRentAlto, yRentAlto, 2);

            // Variable difusa volatilidad que contiene los terminos: bajo, medio, alto
            FuzzyVariable volatilidad = new FuzzyVariable("volatilidad", 0, 0.3, "");
            // Conjunto difuso: Linea recta con coordenadas: (0, 1), (0.05, 0)
            volatilidad.addTerm("bajo", xVolBajo, yVolBajo, 2);
            // Conjunto difuso: Triangulo con coordenadas: (0, 0), (0.15, 1), (0.3, 0)
            volatilidad.addTerm("medio", new TriangleFuzzySet(0, 0.15, 0.25));
            // Conjunto difuso: Linea recta con coordenadas: (0.1, 0), (0.3, 1)
            volatilidad.addTerm("alto", xVolAlto, yVolAlto, 2);

            // Variable difusa propension que contiene los terminos: poco, medianamente, muy
            FuzzyVariable propension = new FuzzyVariable("propension", 0, 1, "");
            // Conjunto difuso: Linea recta con coordenadas: (0, 1), (0.5, 0)
            propension.addTerm("poco", xPropPoco, yPropPoco, 2);
            // Conjunto difuso: Triangulo con coordenadas: (0.2, 0), (0.5, 1), (0.8, 0)
            propension.addTerm("medianamente", new TriangleFuzzySet(0.2, 0.5, 0.8));
            // Conjunto difuso: Linea recta con coordenadas: (0.5, 0), (1, 1)
            propension.addTerm("muy", xPropMuy, yPropMuy, 2);

            // Variable difusa probabilidad de escogencia que contiene los terminos:
            // muy bajo, bajo, medio, alto, muy alto
            FuzzyVariable probabilidadEscogencia = new FuzzyVariable("probabilidad", 0, 1, "");
            // Conjunto difuso: Linea recta con coordenadas: (0, 1), (0.3, 0)
            probabilidadEscogencia.addTerm("muyBajo", xProbMuyBajo, yProbMuyBajo, 2);
            // Conjunto difuso: Triangulo con coordenadas: (0.1, 0), (0.3, 1), (0.5, 0)
            probabilidadEscogencia.addTerm("bajo", new TriangleFuzzySet(0.1, 0.3, 0.5));
            // Conjunto difuso: Triangulo con coordenadas: (0.2, 0), (0.5, 1), (0.8, 0)
            probabilidadEscogencia.addTerm("medio", new TriangleFuzzySet(0.3, 0.5, 0.7));
            // Conjunto difuso: Triangulo con coordenadas: (0.5, 0), (0.7, 1), (0.9, 0)
            probabilidadEscogencia.addTerm("alto", new TriangleFuzzySet(0.5, 0.7, 0.9));
            // Conjunto difuso: Linea recta con coordenadas: (0.7, 0), (1, 1)
            probabilidadEscogencia.addTerm("muyAlto", xProbMuyAlto, yProbMuyAlto, 2);

            // Reglas
            FuzzyRule reglaBajoBajoPoco = new FuzzyRule();
            reglaBajoBajoPoco.addAntecedent(new FuzzyValue(rentabilidad, "bajo"));
            reglaBajoBajoPoco.addAntecedent(new FuzzyValue(volatilidad, "bajo"));
            reglaBajoBajoPoco.addAntecedent(new FuzzyValue(propension, "poco"));
            reglaBajoBajoPoco.addConclusion(new FuzzyValue(probabilidadEscogencia, "bajo"));

            FuzzyRule reglaBajoBajoMed = new FuzzyRule();
            reglaBajoBajoMed.addAntecedent(new FuzzyValue(rentabilidad, "bajo"));
            reglaBajoBajoMed.addAntecedent(new FuzzyValue(volatilidad, "bajo"));
            reglaBajoBajoMed.addAntecedent(new FuzzyValue(propension, "medianamente"));
            reglaBajoBajoMed.addConclusion(new FuzzyValue(probabilidadEscogencia, "medio"));

            FuzzyRule reglaBajoBajoMuy = new FuzzyRule();
            reglaBajoBajoMuy.addAntecedent(new FuzzyValue(rentabilidad, "bajo"));
            reglaBajoBajoMuy.addAntecedent(new FuzzyValue(volatilidad, "bajo"));
            reglaBajoBajoMuy.addAntecedent(new FuzzyValue(propension, "muy"));
            reglaBajoBajoMuy.addConclusion(new FuzzyValue(probabilidadEscogencia, "alto"));

            FuzzyRule reglaBajoMedPoco = new FuzzyRule();
            reglaBajoMedPoco.addAntecedent(new FuzzyValue(rentabilidad, "bajo"));
            reglaBajoMedPoco.addAntecedent(new FuzzyValue(volatilidad, "medio"));
            reglaBajoMedPoco.addAntecedent(new FuzzyValue(propension, "poco"));
            reglaBajoMedPoco.addConclusion(new FuzzyValue(probabilidadEscogencia, "muyBajo"));

            FuzzyRule reglaBajoMedMed = new FuzzyRule();
            reglaBajoMedMed.addAntecedent(new FuzzyValue(rentabilidad, "bajo"));
            reglaBajoMedMed.addAntecedent(new FuzzyValue(volatilidad, "medio"));
            reglaBajoMedMed.addAntecedent(new FuzzyValue(propension, "medianamente"));
            reglaBajoMedMed.addConclusion(new FuzzyValue(probabilidadEscogencia, "bajo"));

            FuzzyRule reglaBajoMedMuy = new FuzzyRule();
            reglaBajoMedMuy.addAntecedent(new FuzzyValue(rentabilidad, "bajo"));
            reglaBajoMedMuy.addAntecedent(new FuzzyValue(volatilidad, "medio"));
            reglaBajoMedMuy.addAntecedent(new FuzzyValue(propension, "muy"));
            reglaBajoMedMuy.addConclusion(new FuzzyValue(probabilidadEscogencia, "medio"));

            FuzzyRule reglaBajoAltoPoco = new FuzzyRule();
            reglaBajoAltoPoco.addAntecedent(new FuzzyValue(rentabilidad, "bajo"));
            reglaBajoAltoPoco.addAntecedent(new FuzzyValue(volatilidad, "alto"));
            reglaBajoAltoPoco.addAntecedent(new FuzzyValue(propension, "poco"));
            reglaBajoAltoPoco.addConclusion(new FuzzyValue(probabilidadEscogencia, "muyBajo"));

            FuzzyRule reglaBajoAltoMed = new FuzzyRule();
            reglaBajoAltoMed.addAntecedent(new FuzzyValue(rentabilidad, "bajo"));
            reglaBajoAltoMed.addAntecedent(new FuzzyValue(volatilidad, "alto"));
            reglaBajoAltoMed.addAntecedent(new FuzzyValue(propension, "medianamente"));
            reglaBajoAltoMed.addConclusion(new FuzzyValue(probabilidadEscogencia, "muyBajo"));

            FuzzyRule reglaBajoAltoMuy = new FuzzyRule();
            reglaBajoAltoMuy.addAntecedent(new FuzzyValue(rentabilidad, "bajo"));
            reglaBajoAltoMuy.addAntecedent(new FuzzyValue(volatilidad, "alto"));
            reglaBajoAltoMuy.addAntecedent(new FuzzyValue(propension, "muy"));
            reglaBajoAltoMuy.addConclusion(new FuzzyValue(probabilidadEscogencia, "bajo"));

            FuzzyRule reglaMedBajoPoco = new FuzzyRule();
            reglaMedBajoPoco.addAntecedent(new FuzzyValue(rentabilidad, "medio"));
            reglaMedBajoPoco.addAntecedent(new FuzzyValue(volatilidad, "bajo"));
            reglaMedBajoPoco.addAntecedent(new FuzzyValue(propension, "poco"));
            reglaMedBajoPoco.addConclusion(new FuzzyValue(probabilidadEscogencia, "medio"));

            FuzzyRule reglaMedBajoMed = new FuzzyRule();
            reglaMedBajoMed.addAntecedent(new FuzzyValue(rentabilidad, "medio"));
            reglaMedBajoMed.addAntecedent(new FuzzyValue(volatilidad, "bajo"));
            reglaMedBajoMed.addAntecedent(new FuzzyValue(propension, "medianamente"));
            reglaMedBajoMed.addConclusion(new FuzzyValue(probabilidadEscogencia, "alto"));

            FuzzyRule reglaMedBajoMuy = new FuzzyRule();
            reglaMedBajoMuy.addAntecedent(new FuzzyValue(rentabilidad, "medio"));
            reglaMedBajoMuy.addAntecedent(new FuzzyValue(volatilidad, "bajo"));
            reglaMedBajoMuy.addAntecedent(new FuzzyValue(propension, "muy"));
            reglaMedBajoMuy.addConclusion(new FuzzyValue(probabilidadEscogencia, "muyAlto"));

            FuzzyRule reglaMedMedPoco = new FuzzyRule();
            reglaMedMedPoco.addAntecedent(new FuzzyValue(rentabilidad, "medio"));
            reglaMedMedPoco.addAntecedent(new FuzzyValue(volatilidad, "medio"));
            reglaMedMedPoco.addAntecedent(new FuzzyValue(propension, "poco"));
            reglaMedMedPoco.addConclusion(new FuzzyValue(probabilidadEscogencia, "bajo"));

            FuzzyRule reglaMedMedMed = new FuzzyRule();
            reglaMedMedMed.addAntecedent(new FuzzyValue(rentabilidad, "medio"));
            reglaMedMedMed.addAntecedent(new FuzzyValue(volatilidad, "medio"));
            reglaMedMedMed.addAntecedent(new FuzzyValue(propension, "medianamente"));
            reglaMedMedMed.addConclusion(new FuzzyValue(probabilidadEscogencia, "medio"));

            FuzzyRule reglaMedMedMuy = new FuzzyRule();
            reglaMedMedMuy.addAntecedent(new FuzzyValue(rentabilidad, "medio"));
            reglaMedMedMuy.addAntecedent(new FuzzyValue(volatilidad, "medio"));
            reglaMedMedMuy.addAntecedent(new FuzzyValue(propension, "muy"));
            reglaMedMedMuy.addConclusion(new FuzzyValue(probabilidadEscogencia, "alto"));

            FuzzyRule reglaMedAltoPoco = new FuzzyRule();
            reglaMedAltoPoco.addAntecedent(new FuzzyValue(rentabilidad, "medio"));
            reglaMedAltoPoco.addAntecedent(new FuzzyValue(volatilidad, "alto"));
            reglaMedAltoPoco.addAntecedent(new FuzzyValue(propension, "poco"));
            reglaMedAltoPoco.addConclusion(new FuzzyValue(probabilidadEscogencia, "muyBajo"));

            FuzzyRule reglaMedAltoMed = new FuzzyRule();
            reglaMedAltoMed.addAntecedent(new FuzzyValue(rentabilidad, "medio"));
            reglaMedAltoMed.addAntecedent(new FuzzyValue(volatilidad, "alto"));
            reglaMedAltoMed.addAntecedent(new FuzzyValue(propension, "medianamente"));
            reglaMedAltoMed.addConclusion(new FuzzyValue(probabilidadEscogencia, "bajo"));

            FuzzyRule reglaMedAltoMuy = new FuzzyRule();
            reglaMedAltoMuy.addAntecedent(new FuzzyValue(rentabilidad, "medio"));
            reglaMedAltoMuy.addAntecedent(new FuzzyValue(volatilidad, "alto"));
            reglaMedAltoMuy.addAntecedent(new FuzzyValue(propension, "muy"));
            reglaMedAltoMuy.addConclusion(new FuzzyValue(probabilidadEscogencia, "medio"));

            FuzzyRule reglaAltoBajoPoco = new FuzzyRule();
            reglaAltoBajoPoco.addAntecedent(new FuzzyValue(rentabilidad, "alto"));
            reglaAltoBajoPoco.addAntecedent(new FuzzyValue(volatilidad, "bajo"));
            reglaAltoBajoPoco.addAntecedent(new FuzzyValue(propension, "poco"));
            reglaAltoBajoPoco.addConclusion(new FuzzyValue(probabilidadEscogencia, "alto"));

            FuzzyRule reglaAltoBajoMed = new FuzzyRule();
            reglaAltoBajoMed.addAntecedent(new FuzzyValue(rentabilidad, "alto"));
            reglaAltoBajoMed.addAntecedent(new FuzzyValue(volatilidad, "bajo"));
            reglaAltoBajoMed.addAntecedent(new FuzzyValue(propension, "medianamente"));
            reglaAltoBajoMed.addConclusion(new FuzzyValue(probabilidadEscogencia, "muyAlto"));

            FuzzyRule reglaAltoBajoMuy = new FuzzyRule();
            reglaAltoBajoMuy.addAntecedent(new FuzzyValue(rentabilidad, "alto"));
            reglaAltoBajoMuy.addAntecedent(new FuzzyValue(volatilidad, "bajo"));
            reglaAltoBajoMuy.addAntecedent(new FuzzyValue(propension, "muy"));
            reglaAltoBajoMuy.addConclusion(new FuzzyValue(probabilidadEscogencia, "muyAlto"));

            FuzzyRule reglaAltoMedPoco = new FuzzyRule();
            reglaAltoMedPoco.addAntecedent(new FuzzyValue(rentabilidad, "alto"));
            reglaAltoMedPoco.addAntecedent(new FuzzyValue(volatilidad, "medio"));
            reglaAltoMedPoco.addAntecedent(new FuzzyValue(propension, "poco"));
            reglaAltoMedPoco.addConclusion(new FuzzyValue(probabilidadEscogencia, "medio"));

            FuzzyRule reglaAltoMedMed = new FuzzyRule();
            reglaAltoMedMed.addAntecedent(new FuzzyValue(rentabilidad, "alto"));
            reglaAltoMedMed.addAntecedent(new FuzzyValue(volatilidad, "medio"));
            reglaAltoMedMed.addAntecedent(new FuzzyValue(propension, "medianamente"));
            reglaAltoMedMed.addConclusion(new FuzzyValue(probabilidadEscogencia, "alto"));

            FuzzyRule reglaAltoMedMuy = new FuzzyRule();
            reglaAltoMedMuy.addAntecedent(new FuzzyValue(rentabilidad, "alto"));
            reglaAltoMedMuy.addAntecedent(new FuzzyValue(volatilidad, "medio"));
            reglaAltoMedMuy.addAntecedent(new FuzzyValue(propension, "muy"));
            reglaAltoMedMuy.addConclusion(new FuzzyValue(probabilidadEscogencia, "muyAlto"));

            FuzzyRule reglaAltoAltoPoco = new FuzzyRule();
            reglaAltoAltoPoco.addAntecedent(new FuzzyValue(rentabilidad, "alto"));
            reglaAltoAltoPoco.addAntecedent(new FuzzyValue(volatilidad, "alto"));
            reglaAltoAltoPoco.addAntecedent(new FuzzyValue(propension, "poco"));
            reglaAltoAltoPoco.addConclusion(new FuzzyValue(probabilidadEscogencia, "bajo"));

            FuzzyRule reglaAltoAltoMed = new FuzzyRule();
            reglaAltoAltoMed.addAntecedent(new FuzzyValue(rentabilidad, "alto"));
            reglaAltoAltoMed.addAntecedent(new FuzzyValue(volatilidad, "alto"));
            reglaAltoAltoMed.addAntecedent(new FuzzyValue(propension, "medianamente"));
            reglaAltoAltoMed.addConclusion(new FuzzyValue(probabilidadEscogencia, "medio"));

            FuzzyRule reglaAltoAltoMuy = new FuzzyRule();
            reglaAltoAltoMuy.addAntecedent(new FuzzyValue(rentabilidad, "alto"));
            reglaAltoAltoMuy.addAntecedent(new FuzzyValue(volatilidad, "alto"));
            reglaAltoAltoMuy.addAntecedent(new FuzzyValue(propension, "muy"));
            reglaAltoAltoMuy.addConclusion(new FuzzyValue(probabilidadEscogencia, "alto"));

            // Verifica que el conjunto difuso no se salga del universo de discurso
            FuzzyValue.setConfineFuzzySetsToUOD(true);

            // Las variables de entrada fuzzyficadas mediante un conjunto difuso
            // en forma de campana de Gauss
            FuzzyValue entradaRentabilidad = new FuzzyValue(rentabilidad, new TriangleFuzzySet(rent-0.05, rent, rent+0.05));
            FuzzyValue entradaVolatilidad = new FuzzyValue(volatilidad, new TriangleFuzzySet(vol-0.05, vol, vol+0.05));
            FuzzyValue entradaPropension = new FuzzyValue(propension, new TriangleFuzzySet(prop-0.05, prop, prop+0.05));

            // Valores de entrada con todas las reglas, y se ejecutan con Mamdani
            // y se hace una union de todos los resultados de cada regla
            reglaBajoBajoPoco.removeAllInputs();
            reglaBajoBajoPoco.addInput(entradaRentabilidad);
            reglaBajoBajoPoco.addInput(entradaVolatilidad);
            reglaBajoBajoPoco.addInput(entradaPropension);
            // Se verifica que las entradas correspondan con los antecedentes
            if (reglaBajoBajoPoco.testRuleMatching()) {
                fvv = reglaBajoBajoPoco.execute(new MamdaniMinMaxMinRuleExecutor());
                salidaGlobal = fvv.fuzzyValueAt(0);
            }

            reglaBajoBajoMed.removeAllInputs();
            reglaBajoBajoMed.addInput(entradaRentabilidad);
            reglaBajoBajoMed.addInput(entradaVolatilidad);
            reglaBajoBajoMed.addInput(entradaPropension);
            if (reglaBajoBajoMed.testRuleMatching()) {
                fvv = reglaBajoBajoMed.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaBajoBajoMuy.removeAllInputs();
            reglaBajoBajoMuy.addInput(entradaRentabilidad);
            reglaBajoBajoMuy.addInput(entradaVolatilidad);
            reglaBajoBajoMuy.addInput(entradaPropension);
            if (reglaBajoBajoMuy.testRuleMatching()) {
                fvv = reglaBajoBajoMuy.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaBajoMedPoco.removeAllInputs();
            reglaBajoMedPoco.addInput(entradaRentabilidad);
            reglaBajoMedPoco.addInput(entradaVolatilidad);
            reglaBajoMedPoco.addInput(entradaPropension);
            if (reglaBajoMedPoco.testRuleMatching()) {
                fvv = reglaBajoMedPoco.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaBajoMedMed.removeAllInputs();
            reglaBajoMedMed.addInput(entradaRentabilidad);
            reglaBajoMedMed.addInput(entradaVolatilidad);
            reglaBajoMedMed.addInput(entradaPropension);
            if (reglaBajoMedMed.testRuleMatching()) {
                fvv = reglaBajoMedMed.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaBajoMedMuy.removeAllInputs();
            reglaBajoMedMuy.addInput(entradaRentabilidad);
            reglaBajoMedMuy.addInput(entradaVolatilidad);
            reglaBajoMedMuy.addInput(entradaPropension);
            if (reglaBajoMedMuy.testRuleMatching()) {
                fvv = reglaBajoMedMuy.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaBajoAltoPoco.removeAllInputs();
            reglaBajoAltoPoco.addInput(entradaRentabilidad);
            reglaBajoAltoPoco.addInput(entradaVolatilidad);
            reglaBajoAltoPoco.addInput(entradaPropension);
            if (reglaBajoAltoPoco.testRuleMatching()) {
                fvv = reglaBajoAltoPoco.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaBajoAltoMed.removeAllInputs();
            reglaBajoAltoMed.addInput(entradaRentabilidad);
            reglaBajoAltoMed.addInput(entradaVolatilidad);
            reglaBajoAltoMed.addInput(entradaPropension);
            if (reglaBajoAltoMed.testRuleMatching()) {
                fvv = reglaBajoAltoMed.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaBajoAltoMuy.removeAllInputs();
            reglaBajoAltoMuy.addInput(entradaRentabilidad);
            reglaBajoAltoMuy.addInput(entradaVolatilidad);
            reglaBajoAltoMuy.addInput(entradaPropension);
            if (reglaBajoAltoMuy.testRuleMatching()) {
                fvv = reglaBajoAltoMuy.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaMedBajoPoco.removeAllInputs();
            reglaMedBajoPoco.addInput(entradaRentabilidad);
            reglaMedBajoPoco.addInput(entradaVolatilidad);
            reglaMedBajoPoco.addInput(entradaPropension);
            if (reglaMedBajoPoco.testRuleMatching()) {
                fvv = reglaMedBajoPoco.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaMedBajoMed.removeAllInputs();
            reglaMedBajoMed.addInput(entradaRentabilidad);
            reglaMedBajoMed.addInput(entradaVolatilidad);
            reglaMedBajoMed.addInput(entradaPropension);
            if (reglaMedBajoMed.testRuleMatching()) {
                fvv = reglaMedBajoMed.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaMedBajoMuy.removeAllInputs();
            reglaMedBajoMuy.addInput(entradaRentabilidad);
            reglaMedBajoMuy.addInput(entradaVolatilidad);
            reglaMedBajoMuy.addInput(entradaPropension);
            if (reglaMedBajoMuy.testRuleMatching()) {
                fvv = reglaMedBajoMuy.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaMedMedPoco.removeAllInputs();
            reglaMedMedPoco.addInput(entradaRentabilidad);
            reglaMedMedPoco.addInput(entradaVolatilidad);
            reglaMedMedPoco.addInput(entradaPropension);
            if (reglaMedMedPoco.testRuleMatching()) {
                fvv = reglaMedMedPoco.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaMedMedMed.removeAllInputs();
            reglaMedMedMed.addInput(entradaRentabilidad);
            reglaMedMedMed.addInput(entradaVolatilidad);
            reglaMedMedMed.addInput(entradaPropension);
            if (reglaMedMedMed.testRuleMatching()) {
                fvv = reglaMedMedMed.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaMedMedMuy.removeAllInputs();
            reglaMedMedMuy.addInput(entradaRentabilidad);
            reglaMedMedMuy.addInput(entradaVolatilidad);
            reglaMedMedMuy.addInput(entradaPropension);
            if (reglaMedMedMuy.testRuleMatching()) {
                fvv = reglaMedMedMuy.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaMedAltoPoco.removeAllInputs();
            reglaMedAltoPoco.addInput(entradaRentabilidad);
            reglaMedAltoPoco.addInput(entradaVolatilidad);
            reglaMedAltoPoco.addInput(entradaPropension);
            if (reglaMedAltoPoco.testRuleMatching()) {
                fvv = reglaMedAltoPoco.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaMedAltoMed.removeAllInputs();
            reglaMedAltoMed.addInput(entradaRentabilidad);
            reglaMedAltoMed.addInput(entradaVolatilidad);
            reglaMedAltoMed.addInput(entradaPropension);
            if (reglaMedAltoMed.testRuleMatching()) {
                fvv = reglaMedAltoMed.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaMedAltoMuy.removeAllInputs();
            reglaMedAltoMuy.addInput(entradaRentabilidad);
            reglaMedAltoMuy.addInput(entradaVolatilidad);
            reglaMedAltoMuy.addInput(entradaPropension);
            if (reglaMedAltoMuy.testRuleMatching()) {
                fvv = reglaMedAltoMuy.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaAltoBajoPoco.removeAllInputs();
            reglaAltoBajoPoco.addInput(entradaRentabilidad);
            reglaAltoBajoPoco.addInput(entradaVolatilidad);
            reglaAltoBajoPoco.addInput(entradaPropension);
            if (reglaAltoBajoPoco.testRuleMatching()) {
                fvv = reglaAltoBajoPoco.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaAltoBajoMed.removeAllInputs();
            reglaAltoBajoMed.addInput(entradaRentabilidad);
            reglaAltoBajoMed.addInput(entradaVolatilidad);
            reglaAltoBajoMed.addInput(entradaPropension);
            if (reglaAltoBajoMed.testRuleMatching()) {
                fvv = reglaAltoBajoMed.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaAltoBajoMuy.removeAllInputs();
            reglaAltoBajoMuy.addInput(entradaRentabilidad);
            reglaAltoBajoMuy.addInput(entradaVolatilidad);
            reglaAltoBajoMuy.addInput(entradaPropension);
            if (reglaAltoBajoMuy.testRuleMatching()) {
                fvv = reglaAltoBajoMuy.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaAltoMedPoco.removeAllInputs();
            reglaAltoMedPoco.addInput(entradaRentabilidad);
            reglaAltoMedPoco.addInput(entradaVolatilidad);
            reglaAltoMedPoco.addInput(entradaPropension);
            if (reglaAltoMedPoco.testRuleMatching()) {
                fvv = reglaAltoMedPoco.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaAltoMedMed.removeAllInputs();
            reglaAltoMedMed.addInput(entradaRentabilidad);
            reglaAltoMedMed.addInput(entradaVolatilidad);
            reglaAltoMedMed.addInput(entradaPropension);
            if (reglaAltoMedMed.testRuleMatching()) {
                fvv = reglaAltoMedMed.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaAltoMedMuy.removeAllInputs();
            reglaAltoMedMuy.addInput(entradaRentabilidad);
            reglaAltoMedMuy.addInput(entradaVolatilidad);
            reglaAltoMedMuy.addInput(entradaPropension);
            if (reglaAltoMedMuy.testRuleMatching()) {
                fvv = reglaAltoMedMuy.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaAltoAltoPoco.removeAllInputs();
            reglaAltoAltoPoco.addInput(entradaRentabilidad);
            reglaAltoAltoPoco.addInput(entradaVolatilidad);
            reglaAltoAltoPoco.addInput(entradaPropension);
            if (reglaAltoAltoPoco.testRuleMatching()) {
                fvv = reglaAltoAltoPoco.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaAltoAltoMed.removeAllInputs();
            reglaAltoAltoMed.addInput(entradaRentabilidad);
            reglaAltoAltoMed.addInput(entradaVolatilidad);
            reglaAltoAltoMed.addInput(entradaPropension);
            if (reglaAltoAltoMed.testRuleMatching()) {
                fvv = reglaAltoAltoMed.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            reglaAltoAltoMuy.removeAllInputs();
            reglaAltoAltoMuy.addInput(entradaRentabilidad);
            reglaAltoAltoMuy.addInput(entradaVolatilidad);
            reglaAltoAltoMuy.addInput(entradaPropension);
            if (reglaAltoAltoMuy.testRuleMatching()) {
                fvv = reglaAltoAltoMuy.execute(new MamdaniMinMaxMinRuleExecutor());
                if (salidaGlobal == null)
                    salidaGlobal = fvv.fuzzyValueAt(0);
                else
                    salidaGlobal = salidaGlobal.fuzzyUnion(fvv.fuzzyValueAt(0));
            }

            //System.out.println(salidaGlobal.plotFuzzyValue("+"));

            // Se defuzzyfica la salida global mediante el centroide
            probabilidad = salidaGlobal.centerOfAreaDefuzzify();

            //System.out.println("Rentabilidad: " + rent + ", Volatilidad: " + vol + ", Propension al riesgo: " + prop + " ==> " + probabilidad);

        } catch (FuzzyException fe) {
          fe.printStackTrace();
        }
        
        return probabilidad;
    }

    /*public static void main(String[] args)
    {
        double r, v, p;

        try {
            String ent;
            BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Por favor ingrese rentabilidad: ");
            ent = entrada.readLine();
            r = Double.parseDouble(ent);

            System.out.print("Por favor ingrese volatilidad: ");
            ent = entrada.readLine();
            v = Double.parseDouble(ent);

            System.out.print("Por favor ingrese propension: ");
            ent = entrada.readLine();
            p = Double.parseDouble(ent);

            System.out.println("Probabilidad: " + new InferenciaCompraCortoPlazo().probabilidadEscogencia(r, v, p));

        } catch (IOException ioe) {
          ioe.printStackTrace();
        }
    }*/
}
