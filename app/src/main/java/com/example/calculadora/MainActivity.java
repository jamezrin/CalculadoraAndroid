package com.example.calculadora;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TipoOperacion operacionActual = TipoOperacion.NULA;
    private boolean operacionReciente = false;

    private final ConfiguracionBoton[] botones = {
            new ConfiguracionBoton(R.id.calc_uno, new OperacionNumero(1)),
            new ConfiguracionBoton(R.id.calc_dos, new OperacionNumero(2)),
            new ConfiguracionBoton(R.id.calc_tres, new OperacionNumero(3)),
            new ConfiguracionBoton(R.id.calc_cuatro, new OperacionNumero(4)),
            new ConfiguracionBoton(R.id.calc_cinco, new OperacionNumero(5)),
            new ConfiguracionBoton(R.id.calc_seis, new OperacionNumero(6)),
            new ConfiguracionBoton(R.id.calc_siete, new OperacionNumero(7)),
            new ConfiguracionBoton(R.id.calc_ocho, new OperacionNumero(8)),
            new ConfiguracionBoton(R.id.calc_nueve, new OperacionNumero(9)),
            new ConfiguracionBoton(R.id.calc_cero, new OperacionNumero(0)),
            new ConfiguracionBoton(R.id.calc_suma, new OperacionGenerica(TipoOperacion.SUMA)),
            new ConfiguracionBoton(R.id.calc_resta, new OperacionGenerica(TipoOperacion.RESTA)),
            new ConfiguracionBoton(R.id.calc_mult, new OperacionGenerica(TipoOperacion.MULTIPLICACION)),
            new ConfiguracionBoton(R.id.calc_div, new OperacionGenerica(TipoOperacion.DIVISION)),
            new ConfiguracionBoton(R.id.calc_igual, new OperacionCalcular()),
            new ConfiguracionBoton(R.id.calc_clear, new OperacionLimpiar()),
            new ConfiguracionBoton(R.id.calc_back, new OperacionVolver()),
            new ConfiguracionBoton(R.id.calc_punto, new OperacionPunto()),
            new ConfiguracionBoton(R.id.calc_salir, new OperacionSalir()),
            new ConfiguracionBoton(R.id.calc_perc, new OperacionPorcentaje()),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView pantalla = findViewById(R.id.calc_pantalla);
        final TextView pantallaPrev = findViewById(R.id.calc_pantalla_prev);
        final TextView pantallaOperacion = findViewById(R.id.calc_pantalla_op);

        for (final ConfiguracionBoton instancia : botones) {
            findViewById(instancia.id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    instancia.getOperacion().ejecutar(pantalla, pantallaPrev, pantallaOperacion);
                }
            });
        }
    }

    private boolean tieneNumeroValido(String string) {
        if (string.length() <= 0) {
            return false;
        }

        try {
            Double.valueOf(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private interface Operacion {
        void ejecutar(TextView pantalla, TextView pantallaPrev, TextView pantallaOperacion);
    }

    private enum TipoOperacion {
        NULA("NULL") {
            @Override
            public double calcular(double numero1, double numero2) {
                return -1;
            }
        },
        SUMA("+") {
            @Override
            public double calcular(double numero1, double numero2) {
                return numero1 + numero2;
            }
        },
        RESTA("-") {
            @Override
            public double calcular(double numero1, double numero2) {
                return numero1 - numero2;
            }
        },
        MULTIPLICACION("*") {
            @Override
            public double calcular(double numero1, double numero2) {
                return numero1 * numero2;
            }
        },
        DIVISION("/") {
            @Override
            public double calcular(double numero1, double numero2) {
                return numero1 / numero2;
            }
        };

        private final String simbolo;
        TipoOperacion(String simbolo) {
            this.simbolo = simbolo;
        }

        public String getSimbolo() {
            return simbolo;
        }

        public abstract double calcular(double numero1, double numero2);
    }

    private final class OperacionGenerica implements Operacion {
        private final TipoOperacion tipoOperacion;
        private final OperacionCalcular calculador = new OperacionCalcular();

        public OperacionGenerica(TipoOperacion tipoOperacion) {
            this.tipoOperacion = tipoOperacion;
        }

        @Override
        public void ejecutar(TextView pantalla, TextView pantallaPrev, TextView pantallaOperacion) {
            if (tieneNumeroValido(pantalla.getText().toString())) {
                if (pantallaPrev.getText().length() > 0) {
                    calculador.ejecutar(pantalla, pantallaPrev, pantallaOperacion);
                    operacionReciente = true;
                } else {
                    pantallaPrev.setText(pantalla.getText());
                    pantalla.setText("");
                }

                operacionActual = tipoOperacion;
                pantallaOperacion.setText(tipoOperacion.getSimbolo());
            } else if (pantalla.getText().length() == 0 && operacionActual == TipoOperacion.NULA && tipoOperacion == TipoOperacion.RESTA) {
                pantalla.setText(tipoOperacion.getSimbolo());
            } else {
                operacionActual = tipoOperacion;
                pantallaOperacion.setText(tipoOperacion.getSimbolo());
            }
        }

        public TipoOperacion getTipoOperacion() {
            return tipoOperacion;
        }
    }

    private final class OperacionCalcular implements Operacion {
        @Override
        public void ejecutar(TextView pantalla, TextView pantallaPrev, TextView pantallaOperacion) {
            if (tieneNumeroValido(pantalla.getText().toString()) && tieneNumeroValido(pantallaPrev.getText().toString())) {
                double numero1 = Double.parseDouble(pantallaPrev.getText().toString());
                double numero2 = Double.parseDouble(pantalla.getText().toString());
                double resultado = operacionActual.calcular(numero1, numero2);

                pantallaPrev.setText("");

                String resultadoPantalla = Double.toString(resultado);
                if (resultado % 1 == 0) {
                    resultadoPantalla = resultadoPantalla.substring(
                            0, resultadoPantalla.indexOf(".")
                    );
                }

                pantalla.setText(resultadoPantalla);
                pantallaOperacion.setText("");
            }
        }
    }

    private final class OperacionLimpiar implements Operacion {
        @Override
        public void ejecutar(TextView pantalla, TextView pantallaPrev, TextView pantallaOperacion) {
            operacionReciente = false;
            operacionActual = TipoOperacion.NULA;
            pantalla.setText("");
            pantallaPrev.setText("");
            pantallaOperacion.setText("");
        }
    }

    private final class OperacionVolver implements Operacion {
        @Override
        public void ejecutar(TextView pantalla, TextView pantallaPrev, TextView pantallaOperacion) {
            int length = pantalla.getText().length();
            if (length > 0) {
                pantalla.setText(
                        pantalla.getText().subSequence(0, length - 1)
                );
            }
        }
    }

    private final class OperacionSalir implements Operacion {
        @Override
        public void ejecutar(TextView pantalla, TextView pantallaPrev, TextView pantallaOperacion) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            // Salir de la app
                            finishAffinity();
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("¿Estas seguro que quieres salir?")
                        .setPositiveButton("Si", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .show();
            }
        }
    }

    private final class OperacionNumero implements Operacion {
        private final int numero;

        public OperacionNumero(int numero) {
            this.numero = numero;
        }


        @Override
        public void ejecutar(TextView pantalla, TextView pantallaPrev, TextView pantallaOperacion) {
            if (operacionReciente) {
                operacionReciente = false;
                pantallaPrev.setText(pantalla.getText());
                pantalla.setText("");
            }

            pantalla.setText(String.format("%s%d", pantalla.getText(), numero));
        }

        public int getNumero() {
            return numero;
        }
    }

    private final class OperacionPunto implements Operacion {
        @Override
        public void ejecutar(TextView pantalla, TextView pantallaPrev, TextView pantallaOperacion) {
            if (!pantalla.getText().toString().contains(".")) {
                pantalla.setText(String.format("%s.", pantalla.getText()));
            }
        }
    }

    private final class OperacionPorcentaje implements Operacion {
        private int contadorInteracciones = 0;

        @Override
        public void ejecutar(TextView pantalla, TextView pantallaPrev, TextView pantallaOperacion) {
            if (contadorInteracciones++ < 3) {
                Toast.makeText(MainActivity.this, "Kitipasa cabron",
                        Toast.LENGTH_LONG
                ).show();
            } else {
                Toast.makeText(MainActivity.this, "Mira eh me cago en tus muertos pesao",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    private final class ConfiguracionBoton {
        private final int id;
        private final Operacion operacion;

        public ConfiguracionBoton(int id, Operacion operacion) {
            this.id = id;
            this.operacion = operacion;
        }

        public int getId() {
            return id;
        }

        public Operacion getOperacion() {
            return operacion;
        }
    }
}
