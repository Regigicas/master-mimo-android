package es.upsa.mimo.datamodule.models;

public class QRModel
{
    private Integer id;
    private String name;

    public QRModel(Integer idJuego, String nombreJuego)
    {
        this.id = idJuego;
        this.name = nombreJuego;
    }

    public Integer getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }
}
