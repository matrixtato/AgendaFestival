package com.agendafestivais.servlet;

import com.agendafestivais.dao.FestivalDAO;
import com.agendafestivais.model.Festival;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/festivais/*")
public class FestivalServlet extends HttpServlet {

    private FestivalDAO dao = new FestivalDAO();
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarRespostaJson(response);

        // Captura o parâmetro de query string para a listagem por pai
        String paiIdParam = request.getParameter("paiId");
        String pathInfo = request.getPathInfo();

        try {
            // Requisito Especial: GET /api/festivais?paiId=2 -> Retorna apenas os festivais pertencentes àquele local
            if (paiIdParam != null) {
                int paiId = Integer.parseInt(paiIdParam);
                List<Festival> listaPorPai = dao.listarPorPai(paiId);
                response.setStatus(HttpServletResponse.SC_OK); // 200
                response.getWriter().print(gson.toJson(listaPorPai));
                return;
            }

            // Cenário comum: Se houver ID na URL (ex: /api/festivais/1) busca por ID específico
            if (pathInfo != null && pathInfo.length() > 1) {
                int id = Integer.parseInt(pathInfo.substring(1));
                Festival festival = dao.buscarPorId(id);

                if (festival != null) {
                    response.setStatus(HttpServletResponse.SC_OK); // 200
                    response.getWriter().print(gson.toJson(festival));
                } else {
                    enviarErro(response, HttpServletResponse.SC_NOT_FOUND, "Festival não encontrado."); // 404
                }
            } else {
                // Cenário comum: Se NÃO houver ID ou parâmetro, lista todos
                List<Festival> lista = dao.listarTodos();
                response.setStatus(HttpServletResponse.SC_OK); // 200
                response.getWriter().print(gson.toJson(lista));
            }
        } catch (NumberFormatException e) {
            enviarErro(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido informado na requisição."); // 400
        } catch (Exception e) {
            enviarErro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno: " + e.getMessage()); // 500
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarRespostaJson(response);
        try {
            String body = lerCorpoJson(request);
            Festival novoFestival = gson.fromJson(body, Festival.class);

            if (novoFestival.getLocalId() <= 0) {
                enviarErro(response, HttpServletResponse.SC_BAD_REQUEST, "É obrigatório associar um localId válido."); // 400
                return;
            }

            Festival festivalSalvo = dao.inserir(novoFestival);
            response.setStatus(HttpServletResponse.SC_CREATED); // 201
            response.getWriter().print(gson.toJson(festivalSalvo));
        } catch (Exception e) {
            enviarErro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao salvar festival: " + e.getMessage()); // 500
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarRespostaJson(response);
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.length() <= 1) {
                enviarErro(response, HttpServletResponse.SC_BAD_REQUEST, "ID do festival não informado na URL."); // 400
                return;
            }

            int id = Integer.parseInt(pathInfo.substring(1));
            String body = lerCorpoJson(request);
            Festival festivalAtualizado = gson.fromJson(body, Festival.class);
            festivalAtualizado.setId(id);

            boolean sucesso = dao.atualizar(festivalAtualizado);
            if (sucesso) {
                response.setStatus(HttpServletResponse.SC_OK); // 200
                response.getWriter().print(gson.toJson(festivalAtualizado));
            } else {
                enviarErro(response, HttpServletResponse.SC_NOT_FOUND, "Impossível atualizar. Festival não encontrado."); // 404
            }
        } catch (NumberFormatException e) {
            enviarErro(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido."); // 400
        } catch (Exception e) {
            enviarErro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao atualizar: " + e.getMessage()); // 500
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarRespostaJson(response);
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.length() <= 1) {
                enviarErro(response, HttpServletResponse.SC_BAD_REQUEST, "ID do festival não informado."); // 400
                return;
            }

            int id = Integer.parseInt(pathInfo.substring(1));
            boolean excluido = dao.excluir(id);

            if (excluido) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204
            } else {
                enviarErro(response, HttpServletResponse.SC_NOT_FOUND, "Festival não encontrado para exclusão."); // 404
            }
        } catch (NumberFormatException e) {
            enviarErro(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido."); // 400
        } catch (Exception e) {
            enviarErro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao excluir: " + e.getMessage()); // 500
        }
    }

    // Métodos Auxiliares Obrigatórios da Diretiva 4.3
    private void configurarRespostaJson(HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
    }

    private String lerCorpoJson(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        String linha;
        try (var reader = request.getReader()) {
            while ((linha = reader.readLine()) != null) {
                sb.append(linha);
            }
        }
        return sb.toString();
    }

    private void enviarErro(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.getWriter().print("{\"status\": " + status + ", \"erro\": \"" + message + "\"}");
    }
}
