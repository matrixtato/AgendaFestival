package com.agendafestivais.servlet;

import com.agendafestivais.dao.LocalDAO;
import com.agendafestivais.model.Local;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/locais/*")
public class LocalServlet extends HttpServlet {

    private LocalDAO dao = new LocalDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarRespostaJson(response);
        String pathInfo = request.getPathInfo();

        try {
            // Se houver ID na URL (ex: /api/locais/1) busca um registro específico
            if (pathInfo != null && pathInfo.length() > 1) {
                int id = Integer.parseInt(pathInfo.substring(1));
                Local local = dao.buscarPorId(id);

                if (local != null) {
                    response.setStatus(HttpServletResponse.SC_OK); // 200
                    response.getWriter().print(gson.toJson(local));
                } else {
                    enviarErro(response, HttpServletResponse.SC_NOT_FOUND, "Local não encontrado."); // 404
                }
            } else {
                // Se NÃO houver ID na URL, lista todos
                List<Local> lista = dao.listarTodos();
                response.setStatus(HttpServletResponse.SC_OK); // 200
                response.getWriter().print(gson.toJson(lista));
            }
        } catch (NumberFormatException e) {
            enviarErro(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido informado na URL."); // 400
        } catch (Exception e) {
            enviarErro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno: " + e.getMessage()); // 500
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarRespostaJson(response);
        try {
            String body = lerCorpoJson(request);
            Local novoLocal = gson.fromJson(body, Local.class);

            if (novoLocal.getNome() == null || novoLocal.getNome().trim().isEmpty()) {
                enviarErro(response, HttpServletResponse.SC_BAD_REQUEST, "O nome do local é obrigatório."); // 400
                return;
            }

            Local localSalvo = dao.inserir(novoLocal);
            response.setStatus(HttpServletResponse.SC_CREATED); // 201
            response.getWriter().print(gson.toJson(localSalvo));
        } catch (Exception e) {
            enviarErro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao inserir: " + e.getMessage()); // 500
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarRespostaJson(response);
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.length() <= 1) {
                enviarErro(response, HttpServletResponse.SC_BAD_REQUEST, "ID do local não informado na URL."); // 400
                return;
            }

            int id = Integer.parseInt(pathInfo.substring(1));
            String body = lerCorpoJson(request);
            Local localAtualizado = gson.fromJson(body, Local.class);
            localAtualizado.setId(id);

            boolean sucesso = dao.atualizar(localAtualizado);
            if (sucesso) {
                response.setStatus(HttpServletResponse.SC_OK); // 200
                response.getWriter().print(gson.toJson(localAtualizado));
            } else {
                enviarErro(response, HttpServletResponse.SC_NOT_FOUND, "Impossível atualizar. Local não encontrado."); // 404
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
                enviarErro(response, HttpServletResponse.SC_BAD_REQUEST, "ID do local não informado."); // 400
                return;
            }

            int id = Integer.parseInt(pathInfo.substring(1));
            boolean excluido = dao.excluir(id);

            if (excluido) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204
            } else {
                enviarErro(response, HttpServletResponse.SC_NOT_FOUND, "Local não encontrado para exclusão."); // 404
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

    private void enviarErro(HttpServletResponse response, int status, String mensagem) throws IOException {
        response.setStatus(status);
        response.getWriter().print("{\"status\": " + status + ", \"erro\": \"" + mensagem + "\"}");
    }
}
