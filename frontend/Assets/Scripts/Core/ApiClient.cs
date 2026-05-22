using System;
using System.Collections;
using System.Text;
using UnityEngine;
using UnityEngine.Networking;

public class ApiClient : MonoBehaviour
{
    [SerializeField] private ApiConfig config;

    private static ApiClient _instance;
    public static ApiClient Instance => _instance;

    private string BaseUrl => config != null ? config.baseUrl : "http://localhost:8080/api";

    void Awake()
    {
        if (_instance == null)
        {
            _instance = this;
            DontDestroyOnLoad(gameObject);
        }
        else
        {
            Destroy(gameObject);
        }
    }

    public IEnumerator Get<T>(string endpoint, Action<T> onSuccess, Action<string> onError = null)
    {
        using var request = UnityWebRequest.Get(BaseUrl + endpoint);
        yield return request.SendWebRequest();
        HandleResponse(request, onSuccess, onError);
    }

    public IEnumerator Post<T>(string endpoint, object body, Action<T> onSuccess, Action<string> onError = null)
    {
        var json = body != null ? JsonUtility.ToJson(body) : "{}";
        yield return SendWithBody("POST", endpoint, json, onSuccess, onError);
    }

    public IEnumerator Put<T>(string endpoint, object body, Action<T> onSuccess, Action<string> onError = null)
    {
        var json = body != null ? JsonUtility.ToJson(body) : "{}";
        yield return SendWithBody("PUT", endpoint, json, onSuccess, onError);
    }

    private IEnumerator SendWithBody<T>(string method, string endpoint, string json, Action<T> onSuccess, Action<string> onError)
    {
        using var request = new UnityWebRequest(BaseUrl + endpoint, method);
        var bodyRaw = Encoding.UTF8.GetBytes(json);
        request.uploadHandler = new UploadHandlerRaw(bodyRaw);
        request.downloadHandler = new DownloadHandlerBuffer();
        request.SetRequestHeader("Content-Type", "application/json");
        yield return request.SendWebRequest();
        HandleResponse(request, onSuccess, onError);
    }

    private void HandleResponse<T>(UnityWebRequest request, Action<T> onSuccess, Action<string> onError)
    {
        if (request.result != UnityWebRequest.Result.Success)
        {
            Debug.LogError($"API Error [{request.url}]: {request.error} | {request.downloadHandler?.text}");
            onError?.Invoke(request.error);
            return;
        }

        try
        {
            var result = JsonUtility.FromJson<T>(request.downloadHandler.text);
            onSuccess(result);
        }
        catch (Exception e)
        {
            Debug.LogError($"JSON parse error: {e.Message}\nRaw: {request.downloadHandler.text}");
            onError?.Invoke("Parse error: " + e.Message);
        }
    }
}
